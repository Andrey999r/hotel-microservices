package com.andrey999r.staynova.general.kafka.payment.dto;

public record CancelPaymentKafkaDto(
    long reservationId, String userLogin, long roomId, long hotelId) {}
