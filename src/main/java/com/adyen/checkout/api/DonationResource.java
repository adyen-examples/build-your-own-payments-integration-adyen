package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.checkout.Amount;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class DonationResource {
    private final Logger log = LoggerFactory.getLogger(DonationResource.class);

    private final ApplicationProperty applicationProperty;

    public DonationResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if (applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }
    }


    @PostMapping("/donations")
    // TODO : Find the proper response type for the method
    public ResponseEntity<> donations(@RequestBody Amount body, @RequestHeader String host, HttpServletRequest request){

        // TODO : Implement the method to perform a donations call

        return ResponseEntity.ok()
            .body(result);
    }
}
