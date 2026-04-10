package com.api.dto;

import java.time.LocalDateTime;

public record ExceptionDto(String message, String exceptionType, LocalDateTime timestamp) {}
