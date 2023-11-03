package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@RestController
@RequestMapping("/api")
public class WebhookResource {
    private final Logger log = LoggerFactory.getLogger(WebhookResource.class);

    private final ApplicationProperty applicationProperty;

    @Autowired
    public WebhookResource(ApplicationProperty applicationProperty) {
        this.applicationProperty = applicationProperty;

        if (this.applicationProperty.getHmacKey() == null) {
            log.warn("ADYEN_HMAC_KEY is UNDEFINED (Webhook cannot be authenticated)");
            throw new RuntimeException("ADYEN_HMAC_KEY is UNDEFINED");
        }
    }

    @PostMapping("/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws IOException {
        // Receive and validate incoming webhook
        return ResponseEntity.ok().body("");
    }

    @PostMapping("/webhooks/giving")
    public ResponseEntity<String> webhooksGiving(@RequestBody String json) throws IOException {
        // Receive and validate incoming giving webhook
        return ResponseEntity.ok().body("");
    }
}