package com.andrey999r.staynova.general.kafka.payment.dto;

public record PaymentCallbackKafkaDto(
    long reservationId, String paymentUrl, boolean success, String errorMessage) {}
