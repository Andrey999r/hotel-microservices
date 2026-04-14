package com.andrey999r.staynova.infrastructure.adapters.out;

import com.andrey999r.staynova.domain.models.NotificationEventType;
import com.andrey999r.staynova.domain.ports.out.NotificationMessageOutputPort;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.kafka.UnsupportedNotificationEvent;
import com.andrey999r.staynova.infrastructure.config.NotificationMessagesProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageAdapter implements NotificationMessageOutputPort {

  private final NotificationMessagesProperties properties;

  @Override
  public String resolveMessage(NotificationEventType eventType) {
    return switch (eventType) {
      case REGISTRATION -> properties.getMessage();
      default -> throw new UnsupportedNotificationEvent(eventType);
    };
  }
}
