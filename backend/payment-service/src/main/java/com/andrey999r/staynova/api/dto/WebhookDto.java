package com.andrey999r.staynova.api.dto;

import jakarta.validation.constraints.NotBlank;

public record WebhookDto(@NotBlank String sessionId, boolean success, String errorMessage) {}
