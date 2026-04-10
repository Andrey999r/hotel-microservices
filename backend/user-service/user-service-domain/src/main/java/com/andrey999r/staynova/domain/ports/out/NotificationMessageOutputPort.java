package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.models.NotificationEventType;

public interface NotificationMessageOutputPort {
  String resolveMessage(NotificationEventType eventType);
}
