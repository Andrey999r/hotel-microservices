package com.andrey999r.staynova.domain.ports.out.requests;

import com.andrey999r.staynova.domain.models.NotificationEventType;
import com.andrey999r.staynova.domain.vo.user.Login;

public record NotificationSendRequest(Login login, NotificationEventType eventType) {}
