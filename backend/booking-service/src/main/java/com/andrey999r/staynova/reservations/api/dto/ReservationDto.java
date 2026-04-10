package com.andrey999r.staynova.reservations.api.dto;

import com.andrey999r.staynova.reservations.dal.ReservationStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.time.LocalDate;

public record ReservationDto(
    @Null Long id,
    @Null String userLogin,
    @NotNull Long roomId,
    @NotNull @FutureOrPresent LocalDate startDate,
    @NotNull @FutureOrPresent LocalDate endDate,
    @Null ReservationStatus reservationStatus,
    @Null String paymentUrl) {}
