package com.adyen.checkout.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;
import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final Checkout checkout;

    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if (applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.checkout = new Checkout(client);
    }
    @PostMapping("/sessions")
    public ResponseEntity<CreateCheckoutSessionResponse> sessions(@RequestHeader String host, @RequestParam String type, HttpServletRequest request) throws IOException, ApiException {
        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
                .currency("EUR")
                .value(10000L); // value is 100€ in minor units

        var checkoutSession = new CreateCheckoutSessionRequest();
        checkoutSession.countryCode("NL");
        checkoutSession.merchantAccount(this.applicationProperty.getMerchantAccount());
        // (optional) set WEB to filter out payment methods available only for this platform
        checkoutSession.setChannel(CreateCheckoutSessionRequest.ChannelEnum.WEB);
        checkoutSession.setReference(orderRef); // required
        checkoutSession.setReturnUrl(request.getScheme() + "://" + host + "/redirect?orderRef=" + orderRef);
        checkoutSession.setAmount(amount);

        // set lineItems required for some payment methods (ie Klarna)
        checkoutSession.setLineItems(Arrays.asList(
                new LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
                new LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones"))
        );

        log.info("REST request to create Adyen Payment Session {}", checkoutSession);
        var response = checkout.sessions(checkoutSession);
        return ResponseEntity.ok().body(response);
    }
}