package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_API_KEY is UNDEFINED");
        }
    }

    @PostMapping("/sessions")
    public ResponseEntity sessions(@RequestHeader String host, @RequestParam String type, HttpServletRequest request) throws IOException, ApiException {
        // TODO : Create a valid sessions request here based on the input of that function
        var response = "";
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/getPaymentMethods")
    public ResponseEntity paymentMethods() throws IOException, ApiException {
        // TODO: Create a request to retrieve payment methods
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/initiatePayment")
    public ResponseEntity payment(@RequestHeader String host, /* @RequestBody PaymentRequest body, */ HttpServletRequest request) throws IOException, ApiException {
        // TODO: Start a payment request
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/submitAdditionalDetails")
    public ResponseEntity additionalDetails(/*@RequestBody PaymentDetailsRequest detailsRequest*/) throws IOException, ApiException {
        // TODO: Make the payment
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }

    @GetMapping("/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult, @RequestParam String orderRef) throws IOException, ApiException {
        // Handle redirect during payment (returnUrl)
        return new RedirectView("redirectUrl?reason=");
    }
}
