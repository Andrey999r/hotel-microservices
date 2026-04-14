package com.andrey999r.staynova.api.dto;

import java.time.LocalDateTime;

public record ExceptionDto(String message, String exceptionType, LocalDateTime timestamp) {}
