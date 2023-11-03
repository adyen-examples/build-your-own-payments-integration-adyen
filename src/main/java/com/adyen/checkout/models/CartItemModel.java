package com.adyen.checkout.models;

import java.io.Serializable;

public class CartItemModel implements Serializable {
    private String name;

    private long amount;

    private String currency;

    public CartItemModel(String name, long amount, String currency) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

}