package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.CreateCheckoutSessionRequest;
import com.adyen.model.checkout.CreateCheckoutSessionResponse;
import com.adyen.model.checkout.LineItem;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
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
    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_API_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_API_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.checkout = new Checkout(client);
    }

    @PostMapping("/sessions")
    public ResponseEntity<CreateCheckoutSessionResponse> sessions(@RequestHeader String host, @RequestParam String type, HttpServletRequest request) throws IOException, ApiException {
        
        // TODO : Create a valid sessions request here based on the input of that function
        var response = "";
        return ResponseEntity.ok().body(response);
    }
}
