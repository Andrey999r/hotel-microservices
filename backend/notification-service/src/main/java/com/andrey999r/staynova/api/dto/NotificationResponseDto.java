package com.andrey999r.staynova.api.dto;

import java.time.Instant;

public record NotificationResponseDto(String message, Instant time) {}
