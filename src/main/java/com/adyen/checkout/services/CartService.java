package com.adyen.checkout.services;

import com.adyen.checkout.models.CartModel;

import java.util.Map;
import java.util.HashMap;

// In-memory storage for the shopper's shopping cart.
public class CartService {
    private Map<String, CartModel> map;

    public CartService() {
        this.map = new HashMap<String, CartModel>();
    }

    public void put(String cartId, CartModel model) {
        map.put(cartId, model);
    }

    public CartModel get(String cartId) {
        return map.get(cartId);
    }

    public void remove(String cartId) {
        map.remove(cartId);
    }

    public boolean contains(String cartId) {
        return map.containsKey(cartId);
    }
}