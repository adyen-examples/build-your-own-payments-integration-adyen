package com.adyen.checkout.models;

import java.util.ArrayList;

public class CartModel {
    private ArrayList<CartItemModel> cartItems;

    public CartModel()
    {
        cartItems = new ArrayList<CartItemModel>();
    }

    public ArrayList<CartItemModel> getCartItems() {
        return cartItems;
    }

    public void addItemToCart(CartItemModel item) {
        cartItems.add(item);
    }
}
