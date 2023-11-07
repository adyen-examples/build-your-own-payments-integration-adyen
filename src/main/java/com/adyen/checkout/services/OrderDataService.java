package com.adyen.checkout.services;

import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDataService {
    @Autowired
    protected HttpSession session;

    private final static String ORDER_DATA = "OrderData";
    private final static String ORDER_PSP_REFERENCE = "OrderPspReference";
    private final static String FIRST_PAYMENT_PSP_REFERENCE = "FirstPaymentPspReference";
    private final static String REMAINING_AMOUNT = "RemainingAmount";

    public boolean hasOrderData() {
        return session.getAttribute(ORDER_DATA) != null && session.getAttribute(ORDER_PSP_REFERENCE) != null;
    }

    public void setOrderData(String orderData, String pspReference, long remainingAmount) {
        session.setAttribute(ORDER_DATA, orderData);
        session.setAttribute(ORDER_PSP_REFERENCE, pspReference);
        session.setAttribute(REMAINING_AMOUNT, remainingAmount);
    }

    public String getOrderData() {
        return (String) session.getAttribute(ORDER_DATA);
    }

    public String getOrderPspReference() {
        return (String) session.getAttribute(ORDER_PSP_REFERENCE);
    }

    public void setFirstPaymentPspReference(String firstPaymentPspReference) {
        session.setAttribute(FIRST_PAYMENT_PSP_REFERENCE, firstPaymentPspReference);
    }

    public String getFirstPaymentPspReference() {
        return (String) session.getAttribute(FIRST_PAYMENT_PSP_REFERENCE);
    }

    public long getRemainingAmount() {
        return (long) session.getAttribute(REMAINING_AMOUNT);
    }

    public void setRemainingAmount(long remainingAmount) {
        session.setAttribute(REMAINING_AMOUNT, remainingAmount);
    }

    public void clearOrderData() {
        session.removeAttribute(ORDER_DATA);
        session.removeAttribute(ORDER_PSP_REFERENCE);
        session.removeAttribute(FIRST_PAYMENT_PSP_REFERENCE);
        session.removeAttribute(REMAINING_AMOUNT);
    }
}