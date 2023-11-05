package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.services.CartService;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
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
import java.util.HashMap;
import java.util.UUID;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final Checkout checkout;

    @Autowired
    private CartService cartService;

    @Autowired
    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_API_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.checkout = new Checkout(client);
    }

    @PostMapping("/getPaymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);

        log.info("REST request to get Adyen payment methods {}", paymentMethodsRequest);
        var response = checkout.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
            .body(response);
    }

    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentsResponse> payments(@RequestHeader String host, @RequestBody PaymentsRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentsRequest();

        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
            .currency("EUR")
            .value(cartService.getTotalAmount());

        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
        paymentRequest.setReference(orderRef);
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef);

        paymentRequest.setAmount(amount);

        var items = cartService.getShoppingCart().getCartItems();

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

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = checkout.payments(paymentRequest);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/submitAdditionalDetails")
    public ResponseEntity<PaymentsDetailsResponse> payments(@RequestBody PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST request to make Adyen payment details {}", detailsRequest);
        var response = checkout.paymentsDetails(detailsRequest);
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
        var detailsRequest = new PaymentsDetailsRequest();

        if (redirectResult != null && !redirectResult.isEmpty()) {
            detailsRequest.setDetails(new HashMap<String, String>() {
                { put("redirectResult",  redirectResult); }
            });
        } else if (payload != null && !payload.isEmpty()) {
            detailsRequest.setDetails(new HashMap<String, String>() {
                { put("payload",  payload); }
            });
        }

        log.info("REST request to handle payment redirect {}", detailsRequest);
        var response = checkout.paymentsDetails(detailsRequest);
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
