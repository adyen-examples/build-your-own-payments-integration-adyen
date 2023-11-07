package com.adyen.checkout.api;
import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.models.requests.PaymentMethodBalanceCheckRequest;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.DonationService;
import com.adyen.checkout.services.OrderDataService;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.OrdersApi;
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
import java.util.*;


/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final PaymentsApi paymentsApi;

    private final OrdersApi ordersApi;

    @Autowired
    private CartService cartService;

    @Autowired
    private DonationService donationService;

    @Autowired
    private OrderDataService orderDataService;

    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
        this.ordersApi = new OrdersApi(client);
    }


    @PostMapping("/balanceCheck")
    public ResponseEntity<BalanceCheckResponse> balanceCheck(@RequestBody PaymentMethodBalanceCheckRequest request) throws IOException, ApiException {
        var balanceCheckRequest = new BalanceCheckRequest();
        balanceCheckRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());

        var amount = new Amount();
        amount.setCurrency("EUR");
        amount.setValue(cartService.getTotalAmount());
        balanceCheckRequest.setAmount(amount);

        balanceCheckRequest.setPaymentMethod(new HashMap<>() {
            { put("brand", request.getPaymentMethod().getBrand()); }
            { put("encryptedCardNumber", request.getPaymentMethod().getEncryptedCardNumber()); }
            { put("encryptedSecurityCode", request.getPaymentMethod().getEncryptedSecurityCode()); }
            { put("type", request.getPaymentMethod().getType()); }
        });

        var response = ordersApi.getBalanceOfGiftCard(balanceCheckRequest);

        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/createOrder")
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) throws IOException, ApiException {
        var createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        createOrderRequest.setReference(UUID.randomUUID().toString());

        var amount = new Amount();
        amount.setCurrency("EUR");
        amount.setValue(cartService.getTotalAmount());
        createOrderRequest.setAmount(amount);

        var response = ordersApi.orders(createOrderRequest);

        orderDataService.setOrderData(response.getOrderData(), response.getPspReference());

        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/cancelOrder")
    public ResponseEntity<CancelOrderResponse> cancelOrder() throws IOException, ApiException {
        var cancelOrderRequest = new CancelOrderRequest();
        cancelOrderRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());

        var order = new EncryptedOrderData();
        order.setOrderData(orderDataService.getOrderData());
        order.setPspReference(orderDataService.getOrderPspReference());
        cancelOrderRequest.setOrder(order);

        var response = ordersApi.cancelOrder(cancelOrderRequest);
        return ResponseEntity.ok()
                .body(response);
    }

    /**
     * {@code POST  /getPaymentMethods} : Get valid payment methods.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
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

    /**
     * {@code POST  /initiatePayment} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the payment response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();

        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
                .currency("EUR")
                .value(cartService.getTotalAmount());

        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB);
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

        // set lineItems required for some payment methods (ie Klarna)
        paymentRequest.setLineItems(lineItems);

        // required for 3ds2 native flow
        paymentRequest.setAdditionalData(Collections.singletonMap("allow3DS2", "true"));
        // required for 3ds2 native flow
        paymentRequest.setOrigin(request.getScheme() + "://" + host );
        // required for 3ds2
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        // required by some issuers for 3ds2
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        // Used for partial orders
        if (orderDataService.getOrderData() != null && orderDataService.getOrderData() != null) {
            var order = new EncryptedOrderData();
            order.setOrderData(orderDataService.getOrderData());
            order.setPspReference(orderDataService.getOrderPspReference());
            paymentRequest.setOrder(order);
            paymentRequest.setOrderReference(orderDataService.getOrderPspReference());
        }

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest);

        if (response.getDonationToken() == null) {
            log.warn("The payments endpoint did not return a donationToken, please enable this in your Customer Area. See README.");
        } else {
            donationService.setDonationTokenAndOriginalPspReference(response.getDonationToken(), response.getPspReference());
        }

        return ResponseEntity.ok()
                .body(response);
    }

    /**
     * {@code POST  /submitAdditionalDetails} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentDetails response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
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