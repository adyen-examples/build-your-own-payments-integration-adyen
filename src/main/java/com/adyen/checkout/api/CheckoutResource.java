package com.adyen.checkout.api;

import java.io.IOException;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.adyen.model.Amount;
import org.springframework.web.servlet.view.RedirectView;
import com.adyen.model.checkout.*;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;


    // TODO: persist this in a map in-memory-cache, so that we do not only support one donation at a given time
    private static final String DONATION_TOKEN = "DonationToken";

    private static final String PAYMENT_ORIGINAL_PSPREFERENCE = "PaymentOriginalPspReference";

    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        // TODO instantiate the Adyen service/client classes to call our endpoints.
    }

    /**
     * {@code POST  /getPaymentMethods} : Get valid payment methods.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/getPaymentMethods")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity paymentMethods() throws IOException, ApiException {
        // TODO Create a valid paymentMethods call
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }

    @PostMapping("/sessions")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity sessions(@RequestHeader String host, @RequestParam String type, HttpServletRequest request) throws IOException, ApiException {
        // TODO : Create a valid sessions request here based on the input of that function
        var response = "";
        return ResponseEntity.ok().body(response);
    }

    /**
     * {@code POST  /initiatePayment} : Start a transaction.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/initiatePayment")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity payments(@RequestHeader String host, @RequestBody PaymentsRequest body, HttpServletRequest request) throws IOException, ApiException {
        // TODO Create a valid payments request
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }

    /**
     * {@code POST  /submitAdditionalDetails} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)}.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/submitAdditionalDetails")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity payments(@RequestBody PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {
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
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult, @RequestParam String orderRef) throws IOException, ApiException {

        PaymentsDetailsRequest paymentsDetailsRequest = new PaymentsDetailsRequest();
        // TODO Request to handle paymentDetails and return a redirect view based off the response
        // To make it easier what needs to happen, we've predefined several views for you.
        // Get the result code from the response and handle it as follows:
        //  var redirectURL = "/result/";
        //        switch (response.getResultCode()) {
        //            case AUTHORISED:
        //                redirectURL += "success";
        //                break;
        //            case PENDING:
        //            case RECEIVED:
        //                redirectURL += "pending";
        //                break;
        //            case REFUSED:
        //                redirectURL += "failed";
        //                break;
        //            default:
        //                redirectURL += "error";
        //                break;
        //        }
        //        return new RedirectView(redirectURL + "?reason=" + response.getResultCode());
        return new RedirectView("");
    }

    /**
     * {@code POST  /donations} : Perform a donation
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body of the response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/donations")
    // TODO : Add the correct return type here for the ResponseEntity
    public ResponseEntity donations(@RequestBody Amount body, @RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        // TODO Create a valid donations request
        var response = "";
        return ResponseEntity.ok()
                .body(response);
    }
}