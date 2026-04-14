package com.andrey999r.staynova.general.exception;

import java.time.LocalDateTime;

public record ExceptionDto(String error, String message, LocalDateTime timestamp) {}
