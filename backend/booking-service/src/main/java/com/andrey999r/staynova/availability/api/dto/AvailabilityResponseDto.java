package com.andrey999r.staynova.availability.api.dto;

import com.andrey999r.staynova.availability.dal.AvailabilityStatus;

public record AvailabilityResponseDto(String message, AvailabilityStatus status) {}
