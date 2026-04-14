package com.andrey999r.staynova.kafka.listeners;

import com.andrey999r.staynova.bl.NotificationService;
import com.andrey999r.staynova.common.kafka.dto.EmailSubscriptionDto;
import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSubscriptionListener {
  private final NotificationService notificationService;

  @KafkaListener(
      topics = KafkaTopics.EMAIL_TOPIC,
      groupId = "notification-service-group"
  )
  public void listenEmailSubscription(EmailSubscriptionDto dto) {
    log.info(
        "Received email subscription event for login={}, enabled={}", dto.login(), dto.enabled());
    notificationService.updateEmailSubscription(dto);
  }
}
