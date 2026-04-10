package com.andrey999r.staynova.infrastructure.adapters.in.web.dto;

import java.util.List;

public record UserInfoResponseDto(String login, String password, List<String> roles) {}
