package com.adyen.checkout.services;

import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.models.CartModel;
import com.adyen.checkout.models.InventoryModel;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;

@Service
public class CartService {
    @Autowired
    protected HttpSession session;

    @Autowired
    private OrderService orderDataService;

    private final static String SHOPPING_CART = "ShoppingCart";

    private HashMap<String, InventoryModel> inventory;

    public CartService() {
        inventory = new HashMap<String, InventoryModel>()
        {
            { put("sunglasses", new InventoryModel("sunglasses", 2999, "EUR")); }
            { put("headphones", new InventoryModel("headphones", 5499, "EUR")); }
        };
    }

    public void createEmptyShoppingCart() {
        if (session.getAttribute(SHOPPING_CART) == null) {
            var cart = new CartModel();
            session.setAttribute(SHOPPING_CART, cart);
        }
    }

    public void clearShoppingCart() {
        session.removeAttribute(SHOPPING_CART);
    }

    public void addItemToCart(String name) {
        if (!inventory.containsKey(name))  {
            return;
        }

        Object obj = session.getAttribute(SHOPPING_CART);

        if (!(obj instanceof CartModel)) {
            return;
        }

        CartModel cart = (CartModel) obj;
        var itemToAdd = inventory.get(name);
        cart.getCartItems().add(new CartItemModel(itemToAdd.name, itemToAdd.amount, itemToAdd.currency));

        session.setAttribute(SHOPPING_CART, cart);
    }

    public CartModel getShoppingCart() {
        Object obj = session.getAttribute(SHOPPING_CART);

        if (obj == null || !(obj instanceof CartModel)) {
            return null;
        }

        return (CartModel) obj;
    }

    public long getTotalAmount(){
        long totalAmount = 0;

        var items = getShoppingCart().getCartItems();

        for (CartItemModel item : items ) {
            totalAmount += item.getAmount();
        }

        return totalAmount;
    }
}