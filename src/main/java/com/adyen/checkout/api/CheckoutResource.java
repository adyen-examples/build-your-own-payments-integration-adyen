package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        // TODO: Instantiate a new Checkout Client here
    }

    @PostMapping("/sessions")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity sessions(@RequestHeader String host, @RequestParam String type, HttpServletRequest request) throws IOException, ApiException {

        // TODO : Create a valid sessions request here based on the input of that function
        var response = "";
        return ResponseEntity.ok().body(response);
    }
}
