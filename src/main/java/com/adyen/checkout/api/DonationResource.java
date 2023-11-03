package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.DonationService;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api")
public class DonationResource {
    private final Logger log = LoggerFactory.getLogger(DonationResource.class);

    private final ApplicationProperty applicationProperty;

    private final PaymentsApi paymentsApi;

    @Autowired
    private DonationService donationService;

    public DonationResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        if (applicationProperty.getApiKey() == null) {
            log.warn("ADYEN_KEY is UNDEFINED");
            throw new RuntimeException("ADYEN_KEY is UNDEFINED");
        }

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.paymentsApi = new PaymentsApi(client);
    }

    /**
     * {@code POST  /donations} : Perform a donation
     *
     * @return the {@link ResponseEntity} with status {@code 200 (Ok)} and with body the donationPaymentResponse response.
     * @throws IOException  from Adyen API.
     * @throws ApiException from Adyen API.
     */
    @PostMapping("/donations")
    public ResponseEntity<DonationPaymentResponse> donations(@RequestBody Amount body, @RequestHeader String host, HttpServletRequest request) throws IOException, ApiException {
        DonationPaymentRequest donationRequest = new DonationPaymentRequest();

        String originalPspReference = donationService.getPaymentOriginalPspReference();
        if (originalPspReference == null) {
            log.info("Could not find the PspReference in the stored session.");
            return ResponseEntity.badRequest().build();
        }

        String donationToken = donationService.getDonationToken();

        if (donationService.getDonationToken() == null) {
            log.info("Could not find the DonationToken in the stored session.");
            return ResponseEntity.badRequest().build();
        }

        donationRequest.amount(body);
        donationRequest.reference(UUID.randomUUID().toString());
        donationRequest.setPaymentMethod(new CheckoutPaymentMethod(new CardDetails()));
        donationRequest.setDonationToken(donationToken.toString());
        donationRequest.donationOriginalPspReference(originalPspReference.toString());
        donationRequest.setDonationAccount(this.applicationProperty.getDonationMerchantAccount());
        donationRequest.returnUrl(request.getScheme() + "://" + host);
        donationRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());
        donationRequest.shopperInteraction(DonationPaymentRequest.ShopperInteractionEnum.CONTAUTH);

        DonationPaymentResponse result = this.paymentsApi.donations(donationRequest);

        return ResponseEntity.ok()
                .body(result);
    }


    public DonationService getDonationService() {
        return donationService;
    }

    public void setDonationService(DonationService donationService) {
        this.donationService = donationService;
    }
}