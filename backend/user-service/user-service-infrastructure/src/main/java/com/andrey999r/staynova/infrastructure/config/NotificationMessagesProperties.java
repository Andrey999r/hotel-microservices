package com.andrey999r.staynova.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "notification.messages")
@Getter
@Setter
public class NotificationMessagesProperties {

  private String message;
}
