package com.andrey999r.staynova.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
@Slf4j
public class FallbackController {
    @GetMapping("/user-service")
    public String handleUserServiceFallback() {
        return "user-service is not available";
    }
    @GetMapping("/booking-service")
    public String handleBookingServiceFallback() {
        return "booking-service is not available";
    }
    @GetMapping("/auth-service")
    public String  handleAuthServiceFallback() {
        return "auth-service is not available";
    }
    @GetMapping("/notification-service")
    public String handleNotificationServiceFallback() {
        return "notification-service is not available";
    }
    @GetMapping("/payment-service")
    public String handlePaymentServiceFallback() {
        return "payment-service is not available";
    }
}
