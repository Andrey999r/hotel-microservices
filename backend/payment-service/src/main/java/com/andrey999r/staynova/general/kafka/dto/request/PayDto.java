package com.andrey999r.staynova.general.kafka.dto.request;

import java.time.LocalDate;

public record PayDto(
    long reservationId,
    String userLogin,
    long hotelId,
    long roomId,
    LocalDate startDate,
    LocalDate endDate) {}
