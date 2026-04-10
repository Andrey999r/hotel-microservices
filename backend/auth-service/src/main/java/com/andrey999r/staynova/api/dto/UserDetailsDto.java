package com.andrey999r.staynova.api.dto;

import java.util.List;

public record UserDetailsDto(String login, String password, List<String> roles) {}
