package com.adyen.checkout.models.requests;

public class PaymentMethod {
    private String brand;
    private String encryptedCardNumber;
    private String encryptedSecurityCode;
    private String type;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEncryptedCardNumber() {
        return encryptedCardNumber;
    }

    public void setEncryptedCardNumber(String encryptedCardNumber) {
        this.encryptedCardNumber = encryptedCardNumber;
    }

    public String getEncryptedSecurityCode() {
        return encryptedSecurityCode;
    }

    public void setEncryptedSecurityCode(String encryptedSecurityCode) {
        this.encryptedSecurityCode = encryptedSecurityCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}