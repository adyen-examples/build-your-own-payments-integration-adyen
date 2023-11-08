package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.DonationService;
import com.adyen.checkout.services.OrderService;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final PaymentsApi paymentsApi;

    @Autowired
    private CartService cartService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_API_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
    }

    @PostMapping("/getPaymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        log.info("REST request to get Adyen payment methods {}", paymentMethodsRequest);
        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();

        var orderRef = UUID.randomUUID().toString();


        // default: shopper needs to pay the full amount
        long remainingAmountToPay = cartService.getTotalAmount();

        // if it's an (partial) order, get the remaining amount to pay
        if (body.getOrder() != null) {
            var amountFromGiftCard = orderService.getAmountFromGiftCard(body.getPaymentMethod());
            if (amountFromGiftCard != null) {
                remainingAmountToPay = amountFromGiftCard.getValue();
            } else {
                remainingAmountToPay = orderService.getRemainingAmount();
            }
        }

        var amount = new Amount()
            .currency("EUR")
            .value(remainingAmountToPay); // pass remainingAmountToPay

        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB);
        paymentRequest.setReference(orderRef);
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef);

        paymentRequest.setAmount(amount);

        var items = cartService.getShoppingCart().getCartItems();

        paymentRequest.setCountryCode("NL");

        var lineItems = new ArrayList<LineItem>();
        for (CartItemModel item : items) {
            lineItems.add(new LineItem()
                .quantity(1L)
                .amountIncludingTax(item.getAmount())
                .description(item.getName()));
        }

        paymentRequest.setLineItems(lineItems);
        paymentRequest.setAdditionalData(Collections.singletonMap("allow3DS2", "true"));
        paymentRequest.setOrigin(request.getScheme() + "://" + host );
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        // sets order (orderData, pspReference and orderReference)
        if (body.getOrder() != null) {
            var order = new EncryptedOrderData()
                    .orderData(body.getOrder().getOrderData())
                    .pspReference(body.getOrder().getPspReference());
            paymentRequest.setOrder(order);
            paymentRequest.setOrderReference(body.getOrderReference());
        }

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest);

        // When a successful response, we set the remaining amount correctly in the orderService.
        if (response.getOrder() != null) {
            switch (response.getResultCode()){
                case AUTHORISED:
                case PENDING:
                case RECEIVED:
                    orderService.setRemainingAmount(response.getOrder().getRemainingAmount().getValue());
                    break;
                default:
                    break;
            }
        }

        if (response.getDonationToken() == null) {
            log.error("The payments endpoint did not return a donationToken, please enable this in your Customer Area. See README.");
        }
        else {
            donationService.setDonationTokenAndOriginalPspReference(response.getDonationToken(), response.getPspReference());
        }

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/submitAdditionalDetails")
    public ResponseEntity<PaymentDetailsResponse> payments(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST request to make Adyen payment details {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    /**
     * {@code GET  /handleShopperRedirect} : Handle redirect during payment.
     *
     * @return the {@link RedirectView} with status {@code 302}
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @GetMapping("/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
        var detailsRequest = new PaymentDetailsRequest();

        PaymentCompletionDetails details = new PaymentCompletionDetails();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            details.redirectResult(redirectResult);
        } else if (payload != null && !payload.isEmpty()) {
            details.payload(payload);
        }
        detailsRequest.setDetails(details);

        log.info("REST request to handle payment redirect {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        var redirectURL = "/result/";
        switch (response.getResultCode()) {
            case AUTHORISED:
                redirectURL += "success";
                break;
            case PENDING:
            case RECEIVED:
                redirectURL += "pending";
                break;
            case REFUSED:
                redirectURL += "failed";
                break;
            default:
                redirectURL += "error";
                break;
        }
        return new RedirectView(redirectURL + "?reason=" + response.getResultCode());
    }
}
