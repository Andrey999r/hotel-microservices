package com.andrey999r.staynova.hotel.bl.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HotelRatingDto(@NotNull @Min(1) @Max(5) Integer rating, String comment) {}
