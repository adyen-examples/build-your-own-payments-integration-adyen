package com.adyen.checkout.api;

import com.adyen.checkout.ApplicationProperty;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.util.HMACValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.SignatureException;

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
        var notificationRequest = NotificationRequest.fromJson(json);
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        if (notificationRequestItem.isPresent()) {

            var item = notificationRequestItem.get();

            try {
                if (getHmacValidator().validateHMAC(item, this.applicationProperty.getHmacKey())) {
                    log.info("""
                            Received webhook with event {} :\s
                            Merchant Reference: {}
                            Alias : {}
                            PSP reference : {}"""
                        , item.getEventCode(), item.getMerchantReference(), item.getAdditionalData().get("alias"), item.getPspReference());

                    // TODO: Add & handle ORDERED_OPENED, ORDER_CLOSED webhooks (& AUTHORISED), print the psp-reference of every partial payment

                } else {
                    log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                    throw new RuntimeException("Invalid HMAC signature");
                }
            } catch (SignatureException e) {
                log.error("Error while validating HMAC Key", e);
            }
        } else {
            log.warn("Empty NotificationItem");
        }
        return ResponseEntity.ok().body("[accepted]");
    }

    @PostMapping("/webhooks/giving")
    public ResponseEntity<String> givingWebhooks(@RequestBody String json) throws IOException {
        var notificationRequest = NotificationRequest.fromJson(json);
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        if (notificationRequestItem.isPresent()) {
            var item = notificationRequestItem.get();

            log.info("""
                            Received webhook with event {} :\s
                            Merchant Account Code: {}
                            PSP reference : {}
                            Donation successful : {}
                            """
                , item.getEventCode(), item.getMerchantAccountCode(), item.getPspReference(), item.isSuccess());

            // consume event asynchronously / perform logic here by sending it to a queue / save it in a database

        } else {
            log.warn("Empty NotificationItem");
        }
        return ResponseEntity.ok().body("[accepted]");
    }

    @Bean
    public HMACValidator getHmacValidator() {
        return new HMACValidator();
    }
}
