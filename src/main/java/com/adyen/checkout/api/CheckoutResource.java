package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.PaymentsDetailsRequest;
import com.adyen.model.checkout.PaymentsRequest;
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
import java.util.HashMap;

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

    @PostMapping("/getPaymentMethods")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity paymentMethods() throws IOException, ApiException {
        // TODO Create a valid paymentMethods call
        var response = "";
        return ResponseEntity.ok()
            .body(response);
    }

    @PostMapping("/initiatePayment")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity payments(@RequestHeader String host, @RequestBody PaymentsRequest body, HttpServletRequest request) throws IOException, ApiException {
        // TODO Create a valid payments request
        var response = "";
        return ResponseEntity.ok()
            .body(response);
    }

    @PostMapping("/submitAdditionalDetails")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity submitAdditionalDetails(@RequestBody PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {
        // TODO Create a valid payments/details request
        var response = "";
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
