package com.andrey999r.staynova.domain.ports.out.requests;

import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;

public record EmailSubscriptionRequest(Login login, Email email, boolean enabled) {}
