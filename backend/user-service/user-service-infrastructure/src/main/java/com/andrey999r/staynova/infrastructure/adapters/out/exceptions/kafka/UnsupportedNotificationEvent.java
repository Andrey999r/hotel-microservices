package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.kafka;

import com.andrey999r.staynova.domain.models.NotificationEventType;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class UnsupportedNotificationEvent extends InfrastructureException {
  public UnsupportedNotificationEvent(NotificationEventType eventType) {
    super(
        "No notification template for event: " + eventType.toString(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
