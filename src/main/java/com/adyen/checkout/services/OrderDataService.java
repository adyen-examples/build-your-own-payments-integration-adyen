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

    public void setOrderData(String orderData, String pspReference) {
        session.setAttribute(ORDER_DATA, orderData);
        session.setAttribute(ORDER_PSP_REFERENCE, pspReference);
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

    public void clearOrderData() {
        session.removeAttribute(ORDER_DATA);
        session.removeAttribute(ORDER_PSP_REFERENCE);
        session.removeAttribute(FIRST_PAYMENT_PSP_REFERENCE);
    }
}