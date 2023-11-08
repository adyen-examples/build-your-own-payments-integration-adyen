package com.adyen.checkout.services;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.enums.Environment;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.OrdersApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;

@Service
public class OrderService {
    private final static String ORDER_REMAINING_AMOUNT = "OrderRemainingAmount";

    private final ApplicationProperty applicationProperty;

    @Autowired
    protected HttpSession session;

    @Autowired
    private CartService cartService;

    private final OrdersApi ordersApi;

    public OrderService(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        var client = new Client(applicationProperty.getApiKey(), Environment.TEST);
        this.ordersApi = new OrdersApi(client);
    }

    public void setRemainingAmount(long remainingAmount) {
        session.setAttribute(ORDER_REMAINING_AMOUNT, remainingAmount);
    }

    public long getRemainingAmount() {
        return (long) session.getAttribute(ORDER_REMAINING_AMOUNT);
    }

    public void clearOrderRemainingAmount() {
        session.removeAttribute(ORDER_REMAINING_AMOUNT);
    }

    public Amount getAmountFromGiftCard(CheckoutPaymentMethod paymentMethod) {
        try {
            if (paymentMethod.getCardDetails().getType().getValue() != "giftcard") {
                return null;
            }

            var balanceCheckRequest = new BalanceCheckRequest();
            balanceCheckRequest.setMerchantAccount(this.applicationProperty.getMerchantAccount());

            var balanceCheckAmount = new Amount()
                    .currency("EUR")
                    .value(getRemainingAmount());
            balanceCheckRequest.setAmount(balanceCheckAmount);

            balanceCheckRequest.setPaymentMethod(new HashMap<>() {
                { put("brand", paymentMethod.getCardDetails().getBrand()); }
                { put("encryptedCardNumber", paymentMethod.getCardDetails().getEncryptedCardNumber()); }
                { put("encryptedSecurityCode", paymentMethod.getCardDetails().getEncryptedSecurityCode()); }
                { put("type", paymentMethod.getCardDetails().getType().getValue()); }
            });

            // sends balance-check request
            var response = ordersApi.getBalanceOfGiftCard(balanceCheckRequest);

            // handles response, determine how much the shopper still needs to pay
            switch (response.getResultCode()) {
                case SUCCESS:
                    return new Amount()
                            .currency("EUR")
                            .value(getRemainingAmount()); // return the remaining amount
                case NOTENOUGHBALANCE:
                    return new Amount()
                            .currency("EUR")
                            .value(response.getBalance().getValue()); // return the amount on your gift card
                case FAILED:
                default:
                    return null;
            }
        } catch (ClassCastException | ApiException | IOException e) {
            return null;
        }
    }
}