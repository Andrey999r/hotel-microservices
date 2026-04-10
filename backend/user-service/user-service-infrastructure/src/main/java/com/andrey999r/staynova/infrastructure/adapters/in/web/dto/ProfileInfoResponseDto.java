package com.andrey999r.staynova.infrastructure.adapters.in.web.dto;

public record ProfileInfoResponseDto(
    String login,
    String email,
    boolean emailNotificationsEnabled,
    String avatarUrl) {}
