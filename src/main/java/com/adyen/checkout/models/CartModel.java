package com.adyen.checkout.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CartModel implements Serializable {
    private ArrayList<CartItemModel> cartItems;

    public CartModel(){
        cartItems = new ArrayList<>();
    }

    public ArrayList<CartItemModel> getCartItems() {
        return cartItems;
    }
}
