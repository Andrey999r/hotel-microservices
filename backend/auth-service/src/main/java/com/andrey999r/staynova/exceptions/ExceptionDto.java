package com.andrey999r.staynova.exceptions;

import java.time.LocalDateTime;

public record ExceptionDto(String message, String detailedMsg, LocalDateTime date) {}
