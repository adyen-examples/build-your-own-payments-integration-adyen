package com.adyen.checkout.services;

import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.models.CartModel;
import com.adyen.checkout.models.InventoryModel;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class DonationService {
    @Autowired
    protected HttpSession session;

    private static final String DONATION_TOKEN = "DonationToken";

    private static final String PAYMENT_ORIGINAL_PSPREFERENCE = "PaymentOriginalPspReference";

    public void setDonationTokenAndOriginalPspReference(String donationToken, String originalPspReference) {
        if (donationToken == null) {
            throw new NullPointerException("donationToken is null");
        }

        session.setAttribute(PAYMENT_ORIGINAL_PSPREFERENCE, originalPspReference);
        session.setAttribute(DONATION_TOKEN, donationToken);
    }

    public String getDonationToken() {
        var donationToken = session.getAttribute(DONATION_TOKEN);
        if (donationToken == null) {
            throw new NotFoundException("Could not find donationToken in the sessions");
        }
        return (String) donationToken;
    }

    public String getPaymentOriginalPspReference() {

        var pspReference = session.getAttribute(PAYMENT_ORIGINAL_PSPREFERENCE);
        if (pspReference == null) {
            throw new NotFoundException("Could not find originalPspReference in the sessions");
        }
        return (String) pspReference;
    }
}