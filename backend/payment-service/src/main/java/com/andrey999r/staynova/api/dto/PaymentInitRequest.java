package com.andrey999r.staynova.api.dto;

import java.time.LocalDate;

public record PaymentInitRequest(
    long reservationId,
    String userLogin,
    long hotelId,
    long roomId,
    LocalDate startDate,
    LocalDate endDate) {}
