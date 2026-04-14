package com.andrey999r.staynova.general.kafka.dto.request;

public record CancelDto(long reservationId, String userLogin, long roomId, long hotelId) {}
