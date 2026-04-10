package com.andrey999r.staynova.general.kafka.payment.dto;

import java.time.LocalDate;

public record PaymentRequestKafkaDto(
    long reservationId,
    String userLogin,
    long hotelId,
    long roomId,
    LocalDate startDate,
    LocalDate endDate) {}
