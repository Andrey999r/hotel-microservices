package com.andrey999r.staynova.general.kafka.notification;


import java.time.Duration;

import com.andrey999r.staynova.common.kafka.dto.NotificationKafkaDto;
import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationProducer implements NotificationPublisher {

  private static final Duration DEDUP_TTL = Duration.ofMinutes(10);

  private final KafkaTemplate<String, NotificationKafkaDto> kafkaTemplate;
  private final StringRedisTemplate redisTemplate;

  @Override
  public void send(String login, String message, NotificationEventType eventType) {
    String dedupKey = buildDedupKey(login, message, eventType);

    Boolean alreadySent = redisTemplate.hasKey(dedupKey);
    if (alreadySent) {
      log.warn(
          "Duplicate Kafka message suppressed for user={}, eventType={}, key={}",
          login,
          eventType,
          dedupKey);
      return;
    }

    NotificationKafkaDto dto = new NotificationKafkaDto(login, message, eventType);
    kafkaTemplate.send(KafkaTopics.NOTIFICATION_TOPIC, dto);

    redisTemplate.opsForValue().set(dedupKey, "1", DEDUP_TTL);

    log.info("Sent notification event: {} for {}", eventType, login);
  }

  private String buildDedupKey(String login, String message, NotificationEventType eventType) {
    int hash = (login + message + eventType.name()).hashCode();
    return "kafka:dedup:" + login + ":" + eventType.name() + ":" + hash;
  }
}
