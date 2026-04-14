package com.andrey999r.staynova.kafka.listeners;

import com.andrey999r.staynova.bl.NotificationService;

import com.andrey999r.staynova.common.kafka.dto.NotificationKafkaDto;
import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {
  private final NotificationService notificationService;

  @KafkaListener(
      topics = KafkaTopics.NOTIFICATION_TOPIC,
      groupId = "notification-service-group"
  )
  public void listen(NotificationKafkaDto dto) {
    log.info("Received notification event: {} for {}", dto.eventType(), dto.login());
    notificationService.publishNotification(dto);
  }
}
