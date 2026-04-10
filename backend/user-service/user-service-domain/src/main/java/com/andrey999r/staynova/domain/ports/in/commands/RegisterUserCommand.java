package com.andrey999r.staynova.domain.ports.in.commands;

import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.Password;

public record RegisterUserCommand(
    Login login, Email email, Password password, String adminSecret) {}
