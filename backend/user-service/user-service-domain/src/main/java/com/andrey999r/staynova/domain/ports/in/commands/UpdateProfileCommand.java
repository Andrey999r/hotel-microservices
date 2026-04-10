package com.andrey999r.staynova.domain.ports.in.commands;

import com.andrey999r.staynova.domain.vo.user.Email;

public record UpdateProfileCommand(Email email, boolean emailSubscriptionStatus) {}
