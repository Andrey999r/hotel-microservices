package com.andrey999r.staynova.reservations.api.dto;

public record ReservationFilterDto(Long roomId, Integer pageSize, Integer pageNumber) {}
