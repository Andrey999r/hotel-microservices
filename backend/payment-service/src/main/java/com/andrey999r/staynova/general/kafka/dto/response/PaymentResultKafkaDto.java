package com.andrey999r.staynova.general.kafka.dto.response;

public record PaymentResultKafkaDto(
    long reservationId, String paymentUrl, boolean success, String errorMessage) {}
