package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.OrderService;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class OrderResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

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

        // TODO: instantiate or get your Adyen.client and the client that can send the request to the adyen platform
    }

    // TODO : create your balance check request and response
    @PostMapping("/balanceCheck")
    public ResponseEntity balanceCheck() throws IOException, ApiException {
        var response = "";

        return ResponseEntity.ok()
                .body(response);
    }

    // TODO : create your order request and response, set the remaining amount to be paid when a new order is created
    @PostMapping("/createOrder")
    public ResponseEntity createOrder() throws IOException, ApiException {
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }

    // TODO : create your cancel order request and response, clear your remaining amount to be paid when an order is cancelled
    @PostMapping("/cancelOrder")
    public ResponseEntity cancelOrder() throws IOException, ApiException {

        var response = "";

        return ResponseEntity.ok()
                .body(response);
    }

}