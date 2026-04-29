package com.andrey999r.staynova.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {

    @RequestMapping("/user-service")
    public ResponseEntity<String> handleUserServiceFallback() {
        log.warn("Fallback: user-service is not available");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("user-service is not available");
    }

    @RequestMapping("/booking-service")
    public ResponseEntity<String> handleBookingServiceFallback() {
        log.warn("Fallback: booking-service is not available");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("booking-service is not available");
    }

    @RequestMapping("/auth-service")
    public ResponseEntity<String> handleAuthServiceFallback() {
        log.warn("Fallback: auth-service is not available");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("auth-service is not available");
    }

    @RequestMapping("/notification-service")
    public ResponseEntity<String> handleNotificationServiceFallback() {
        log.warn("Fallback: notification-service is not available");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("notification-service is not available");
    }

    @RequestMapping("/payment-service")
    public ResponseEntity<String> handlePaymentServiceFallback() {
        log.warn("Fallback: payment-service is not available");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body("payment-service is not available");
    }
}
