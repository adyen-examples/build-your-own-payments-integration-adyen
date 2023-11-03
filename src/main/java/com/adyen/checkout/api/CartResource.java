package com.adyen.checkout.api;

import com.adyen.Client;
import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.models.CartModel;
import com.adyen.checkout.models.CartRequest;
import com.adyen.checkout.models.CartResponse;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.InventoryService;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.*;
import com.adyen.service.Checkout;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * REST controller for using Adyen checkout API
 */
@RestController
@RequestMapping("/api/cart")
public class CartResource {
    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    private final ApplicationProperty applicationProperty;

    private final CartService cartService;
    private final InventoryService inventoryService;

    public CartResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;

        this.cartService = new CartService();
        this.inventoryService = new InventoryService();
    }

    @GetMapping("/add")
    public ResponseEntity<CartResponse> add(@RequestParam(required = true) String cartId, @RequestParam(required = true) String name) throws IOException, ApiException {
        var cartResponse = new CartResponse();

        // Check inventory
        var item = inventoryService.getMap().get(name);

        if (item == null)
        {
            return ResponseEntity.notFound().build();
        }

        // Check cart
        var cart = cartService.get(cartId);

        var itemToAdd = new CartItemModel(item.name, item.amount, item.currency);
        if (cart == null) {
            var newCart = new CartModel();
            newCart.addItemToCart(itemToAdd);
            cartService.put(cartId, newCart);
        } else {
            cart.addItemToCart(itemToAdd);
        }

        cartResponse.cart = cart;
        return ResponseEntity.ok()
                .body(cartResponse);
    }

    @GetMapping("/get")
    public ResponseEntity<CartResponse> get(@RequestParam(required = true) String cartId) throws IOException, ApiException {
        var cartResponse = new CartResponse();

        var cart = cartService.get(cartId);

        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        cartResponse.cart = cart;
        return ResponseEntity.ok()
                .body(cartResponse);
    }
}