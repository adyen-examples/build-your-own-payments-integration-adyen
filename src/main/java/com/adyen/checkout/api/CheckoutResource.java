package com.adyen.checkout.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.Amount;
import com.adyen.model.modification.DonationRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Checkout;
import com.adyen.service.Modification;
import com.adyen.service.resource.checkout.PaymentsDetails;
import com.adyen.service.resource.modification.Donate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.exception.ApiException;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {
    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private final ApplicationProperty applicationProperty;

    private final Checkout checkout;

    private final Modification modification;

    // TODO: persist this in a map in-memory-cache, so that we do not only support one donation at a given time
    private static final String DONATION_TOKEN = "DonationToken";

    private static final String PAYMENT_ORIGINAL_PSPREFERENCE = "PaymentOriginalPspReference";

    public CheckoutResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if(applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.checkout = new Checkout(client);
        this.modification = new Modification(client);
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
        var response = checkout.paymentMethods(paymentMethodsRequest);
        return ResponseEntity.ok()
                .body(response);
    }

    /**
     * {@code POST  /initiatePayment} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/initiatePayment")
    public ResponseEntity<PaymentsResponse> payments(@RequestHeader String host, @RequestBody PaymentsRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentsRequest();

        var orderRef = UUID.randomUUID().toString();
        var amount = new Amount()
                .currency("EUR")
                .value(10000L);

        paymentRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        paymentRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
        paymentRequest.setReference(orderRef);
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/api/handleShopperRedirect?orderRef=" + orderRef);

        paymentRequest.setAmount(amount);
        // set lineItems required for some payment methods (ie Klarna)
        paymentRequest.setLineItems(Arrays.asList(
                new LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
                new LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones"))
        );
        // required for 3ds2 native flow
        paymentRequest.setAdditionalData(Collections.singletonMap("allow3DS2", "true"));
        // required for 3ds2 native flow
        paymentRequest.setOrigin(request.getScheme() + "://" + host );
        // required for 3ds2
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        // required by some issuers for 3ds2
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        log.info("REST request to make Adyen payment {}", paymentRequest);
        var response = checkout.payments(paymentRequest);

        var session = request.getSession();
        if (response.getDonationToken() == null) {
            log.error("The payments endpoint did not return a donationToken, please enable this in your Customer Area. See README.");
        }
        else {
            session.setAttribute(PAYMENT_ORIGINAL_PSPREFERENCE, response.getPspReference());
            session.setAttribute(DONATION_TOKEN, response.getDonationToken());
        }

        return ResponseEntity.ok()
                .body(response);
    }

    /**
     * {@code POST  /submitAdditionalDetails} : Make a payment.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the paymentMethods response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/submitAdditionalDetails")
    public ResponseEntity<PaymentsDetailsResponse> payments(@RequestBody PaymentsDetailsRequest detailsRequest) throws IOException, ApiException {
        log.info("REST request to make Adyen payment details {}", detailsRequest);
        var response = checkout.paymentsDetails(detailsRequest);
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
        var detailsRequest = new PaymentsDetailsRequest();

        /*
        PaymentCompletionDetails details = new PaymentDetails();
        if (redirectResult != null && !redirectResult.isEmpty()) {
            details.redirectResult(redirectResult);
        } else if (payload != null && !payload.isEmpty()) {
            details.payload(payload);
        }*/
        detailsRequest.setDetails(new HashMap<>()
        {
            // TODO putDetails here based off ^
        });
        return getRedirectView(detailsRequest);
    }

    private RedirectView getRedirectView(final PaymentsDetailsRequest detailsRequest) throws ApiException, IOException {
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

    /**
     * {@code POST  /donations} : Perform a donation
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the donationPaymentResponse response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/donations")
    public ResponseEntity donations(@RequestBody Amount body, @RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        DonationRequest donationRequest = new DonationRequest();
        HttpSession session = request.getSession();
        var pspReference = session.getAttribute(PAYMENT_ORIGINAL_PSPREFERENCE);
        var donationToken = session.getAttribute(DONATION_TOKEN);

        if (pspReference == null) {
            log.info("Could not find the PspReference in the stored session.");
            return ResponseEntity.badRequest().build();
        }

        if (donationToken == null) {
            log.info("Could not find the DonationToken in the stored session.");
            return ResponseEntity.badRequest().build();
        }

        var amount = new Amount();
        amount.setCurrency(body.getCurrency());
        amount.setValue(body.getValue());

        donationRequest.setModificationAmount(amount);
        donationRequest.reference(UUID.randomUUID().toString());
        //donationRequest.setPaymentMethod(new CheckoutPaymentMethod(new CardDetails()));
        //donationRequest.setDonationToken(donationToken.toString());
        donationRequest.setOriginalReference(pspReference.toString());
        //donationRequest.setDonationAccount(this.applicationProperty.getDonationMerchantAccount());
        //donationRequest.returnUrl(request.getScheme() + "://" + host);
        donationRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        //donationRequest.shopperInteraction(DonationPaymentRequest.ShopperInteractionEnum.CONTAUTH);

        ModificationResult result = this.modification.donate(donationRequest);

        return ResponseEntity.ok()
                .body(result);
    }
}