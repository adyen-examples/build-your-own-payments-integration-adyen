package com.adyen.checkout.models;

public class CartItemModel {
    private String name;
    private long amount;
    private String currency;

    public CartItemModel(String name, long amount, String currency) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }
}