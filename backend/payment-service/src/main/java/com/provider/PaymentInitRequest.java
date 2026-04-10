package com.provider;

import java.time.LocalDate;

public record PaymentInitRequest(
    long reservationId,
    String userLogin,
    long hotelId,
    long roomId,
    LocalDate startDate,
    LocalDate endDate) {}
