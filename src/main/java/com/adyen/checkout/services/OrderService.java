package com.adyen.checkout.services;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final static String ORDER_REMAINING_AMOUNT = "OrderRemainingAmount";

    @Autowired
    protected HttpSession session;

    public void setRemainingAmount(long remainingAmount) {
        session.setAttribute(ORDER_REMAINING_AMOUNT, remainingAmount);
    }

    public long getRemainingAmount() {
        return (long) session.getAttribute(ORDER_REMAINING_AMOUNT);
    }

    public void clearOrderRemainingAmount() {
        session.removeAttribute(ORDER_REMAINING_AMOUNT);
    }
}