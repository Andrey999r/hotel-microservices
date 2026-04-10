package com.andrey999r.staynova.infrastructure.adapters.out.kafka;

import com.andrey999r.staynova.common.kafka.dto.EmailSubscriptionDto;
import com.andrey999r.staynova.common.kafka.dto.NotificationKafkaDto;
import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;
import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import com.andrey999r.staynova.domain.ports.out.EmailSubscriptionOutputPort;
import com.andrey999r.staynova.domain.ports.out.NotificationMessageOutputPort;
import com.andrey999r.staynova.domain.ports.out.NotificationOutputPort;
import com.andrey999r.staynova.domain.ports.out.requests.EmailSubscriptionRequest;
import com.andrey999r.staynova.domain.ports.out.requests.NotificationSendRequest;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.infrastructure.adapters.out.kafka.mappers.EmailSubscriptionKafkaMapper;
import com.andrey999r.staynova.infrastructure.adapters.out.kafka.mappers.NotificationEventTypeMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOutputAdapter implements NotificationOutputPort, EmailSubscriptionOutputPort {

  private static final Duration DEDUP_TTL = Duration.ofMinutes(10);

  private final KafkaTemplate<String, NotificationKafkaDto> notificationKafkaTemplate;
  private final KafkaTemplate<String, EmailSubscriptionDto> emailKafkaTemplate;
  private final StringRedisTemplate stringRedisTemplate;
  private final NotificationMessageOutputPort notificationMessageOutputPort;
  private final NotificationEventTypeMapper notificationEventTypeMapper;
  private final EmailSubscriptionKafkaMapper emailSubscriptionKafkaMapper;

  @Override
  public void sendSubscriptionInfo(EmailSubscriptionRequest emailSubscriptionInfo) {
    Login login = emailSubscriptionInfo.login();
    Email email = emailSubscriptionInfo.email();
    boolean enabled = emailSubscriptionInfo.enabled();
    String dedupKey = buildDedupKey(login.login(), email.email(), enabled);
    if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(dedupKey))) {
      log.warn("Duplicate EmailSubscription suppressed for login={} enabled={}", login, enabled);
      return;
    }
    emailKafkaTemplate.send(
        KafkaTopics.EMAIL_TOPIC, emailSubscriptionKafkaMapper.toDto(emailSubscriptionInfo));
    stringRedisTemplate.opsForValue().set(dedupKey, "1", DEDUP_TTL);
    log.info("EmailSubscription sent to Kafka for login={} enabled={}", login, enabled);
  }

  @Override
  public void sendNotificationInfo(NotificationSendRequest request) {
    com.andrey999r.staynova.domain.models.NotificationEventType domainEventType =
        request.eventType();
    String message = notificationMessageOutputPort.resolveMessage(domainEventType);
    NotificationEventType kafkaEventType = notificationEventTypeMapper.toKafkaType(domainEventType);

    NotificationKafkaDto dto =
        new NotificationKafkaDto(request.login().login(), message, kafkaEventType);

    notificationKafkaTemplate.send(KafkaTopics.NOTIFICATION_TOPIC, dto);
    log.info(
        "Notification sent to Kafka: eventType={} login={}", domainEventType, request.login());
  }

  private String buildDedupKey(String login, String email, boolean enabled) {
    int hash = (login + email + enabled).hashCode();
    return "kafka:dedup:email:" + login + ":" + enabled + ":" + hash;
  }
}
