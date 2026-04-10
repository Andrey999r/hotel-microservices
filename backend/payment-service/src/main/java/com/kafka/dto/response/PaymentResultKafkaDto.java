package com.kafka.dto.response;

public record PaymentResultKafkaDto(
    long reservationId, String paymentUrl, boolean success, String errorMessage) {}
