package com.andrey999r.staynova.infrastructure.adapters.in.web.dto;

import java.time.LocalDateTime;

public record ExceptionDto(String msg, String detailedMsg, LocalDateTime timeStamp) {}
