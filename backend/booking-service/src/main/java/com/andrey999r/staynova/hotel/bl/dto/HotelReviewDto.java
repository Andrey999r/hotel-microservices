package com.andrey999r.staynova.hotel.bl.dto;

import java.time.LocalDateTime;

public record HotelReviewDto(
    String userLogin,
    String roomTitle,
    Long nights,
    Integer rating,
    String comment,
    LocalDateTime createdAt) {}
