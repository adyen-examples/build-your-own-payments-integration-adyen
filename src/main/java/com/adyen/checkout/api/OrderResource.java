package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.requests.PaymentMethodBalanceCheckRequest;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.OrderService;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.OrdersApi;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class OrderResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final OrdersApi ordersApi;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    public OrderResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
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
    public ResponseEntity<CreateOrderResponse> createOrder() throws IOException, ApiException {
        var createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        createOrderRequest.setReference(UUID.randomUUID().toString());

        var amount = new Amount()
                .currency("EUR")
                .value(cartService.getTotalAmount());
        createOrderRequest.setAmount(amount);

        var response = ordersApi.orders(createOrderRequest);

        orderService.setRemainingAmount(response.getRemainingAmount().getValue());

        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/cancelOrder")
    public ResponseEntity<CancelOrderResponse> cancelOrder(EncryptedOrderData encryptedOrderData) throws IOException, ApiException {
        var cancelOrderRequest = new CancelOrderRequest();
        cancelOrderRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());

        cancelOrderRequest.setOrder(encryptedOrderData);

        var response = ordersApi.cancelOrder(cancelOrderRequest);

        orderService.clearOrderRemainingAmount();

        return ResponseEntity.ok()
                .body(response);
    }

}
