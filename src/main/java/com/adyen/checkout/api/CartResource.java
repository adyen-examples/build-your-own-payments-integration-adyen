package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartModel;
import com.adyen.checkout.services.CartService;
import com.adyen.service.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

/**
 * REST controller for managing your shopping cart.
 */
@RestController
@RequestMapping("/api/cart")
public class CartResource {
    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    private CartService cartService;

    public CartResource(ApplicationProperty applicationProperty) {

        this.applicationProperty = applicationProperty;
    }

    @GetMapping("/add")
    public RedirectView add(@RequestParam(required = true) String name, @RequestParam(required = true) String type) throws IOException, ApiException {
        getCartService().addItemToCart(name);
        return new RedirectView("/preview?type=" + type);
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(CartService cartService) {
        this.cartService = cartService;
    }
}