package com.adyen.checkout.web;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.checkout.models.CartItemModel;
import com.adyen.checkout.services.CartService;
import com.adyen.checkout.services.OrderDataService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.UUID;

@Controller
public class CheckoutController {
    private final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private ApplicationProperty applicationProperty;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderDataService orderDataService;

    @Autowired
    public CheckoutController(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getClientKey() == null) {
            log.warn("ADYEN_CLIENT_KEY is undefined ");
            throw new RuntimeException("ADYEN_CLIENT_KEY is UNDEFINED");
        }
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String type, Model model) {
        model.addAttribute("type", type);

        // Create an empty shopping cart stored in your http cookie session.
        cartService.createEmptyShoppingCart();
        model.addAttribute("cart", cartService.getShoppingCart());
        model.addAttribute("totalAmount", cartService.getTotalAmount());

        return "preview";
    }

    @GetMapping("/giftcard")
    public String giftcard(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());

        // Create an empty shopping cart stored in your http cookie session.
        cartService.createEmptyShoppingCart();
        model.addAttribute("cart", cartService.getShoppingCart());
        model.addAttribute("totalAmount", cartService.getTotalAmount());

        return "giftcard";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        model.addAttribute("totalAmount", cartService.getTotalAmount());
        return "checkout";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());

        if (type.equals("success")) {
            cartService.clearShoppingCart();
            orderDataService.clearOrderData();
        }

        return "result";
    }

    @GetMapping("/redirect")
    public String redirect(Model model) {
        model.addAttribute("clientKey", this.applicationProperty.getClientKey());
        return "redirect";
    }
}