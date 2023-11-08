package com.adyen.checkout.models;

public class InventoryModel {
    public String name;
    public long amount;
    public String currency;

    public InventoryModel(String name, long amount, String currency) {
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }
}