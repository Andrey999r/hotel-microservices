package com.kafka.dto.request;

public record CancelDto(long reservationId, String userLogin, long roomId, long hotelId) {}
