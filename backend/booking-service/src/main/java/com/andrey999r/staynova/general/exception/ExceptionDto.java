package com.andrey999r.staynova.general.exception;

import java.time.LocalDateTime;

public record ExceptionDto(String message, String detailedMsg, LocalDateTime date) {}
