package com.andrey999r.staynova.bl.impls;

import com.andrey999r.staynova.api.dto.NotificationResponseDto;
import com.andrey999r.staynova.bl.NotificationSender;
import com.andrey999r.staynova.bl.NotificationService;
import com.andrey999r.staynova.common.kafka.dto.DeviceSubscriptionDto;
import com.andrey999r.staynova.common.kafka.dto.EmailSubscriptionDto;
import com.andrey999r.staynova.common.kafka.dto.NotificationKafkaDto;
import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.dal.DeviceSubscriptionRepository;
import com.andrey999r.staynova.dal.NotificationRepository;
import com.andrey999r.staynova.dal.RecipientRepository;
import com.andrey999r.staynova.dal.entities.DeviceSubscriptionEntity;
import com.andrey999r.staynova.dal.entities.NotificationEntity;
import com.andrey999r.staynova.dal.entities.RecipientEntity;
import com.andrey999r.staynova.general.exception.custom.auth.UserNotAuthenticatedException;
import com.andrey999r.staynova.general.exception.custom.subscription.SubscriptionAlreadyExistsException;
import com.andrey999r.staynova.general.exception.custom.subscription.SubscriptionNotFoundException;
import com.andrey999r.staynova.general.security.SecurityUtils;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final RecipientRepository recipientRepository;
  private final DeviceSubscriptionRepository subscriptionRepository;
  private final List<NotificationSender> senders;
  private final SecurityUtils securityUtils;

  @Override
  public void publishNotification(NotificationKafkaDto dto) {
    log.info("Publishing notification for login={}, eventType={}", dto.login(), dto.eventType());

    RecipientEntity recipientEntity =
        recipientRepository
            .findByLogin(dto.login())
            .orElseGet(
                () -> {
                  log.debug("Recipient not found, creating new: login={}", dto.login());
                  RecipientEntity newRecipient = new RecipientEntity();
                  newRecipient.setLogin(dto.login());
                  return recipientRepository.save(newRecipient);
                });

    NotificationEntity notificationEntity = new NotificationEntity();
    notificationEntity.setMessage(dto.message());
    notificationEntity.setEventType(dto.eventType());
    notificationEntity.setCreatedAt(Instant.now());
    notificationEntity.setRecipient(recipientEntity);
    notificationRepository.save(notificationEntity);

    List<DeviceSubscriptionEntity> subscriptions =
        subscriptionRepository.findAllByLogin(dto.login());
    log.debug("Found {} subscriptions for login={}", subscriptions.size(), dto.login());

    for (DeviceSubscriptionEntity subscription : subscriptions) {
      if (subscription.getChannelType() == ChannelType.EMAIL
          && !recipientEntity.isEmailNotificationsEnabled()) {
        log.debug("Skipping EMAIL channel — notifications disabled for login={}", dto.login());
        continue;
      }
      senders.stream()
          .filter(s -> s.supports() == subscription.getChannelType())
          .findFirst()
          .ifPresent(s -> s.send(subscription, dto.message()));
    }

    log.info(
        "Notification published successfully for login={}, eventType={}",
        dto.login(),
        dto.eventType());
  }

  @Override
  public List<NotificationResponseDto> getAllNotifications() {
    String login = securityUtils.getUserLogin();
    if (login == null) {
      log.warn("getAllNotifications called by unauthenticated user");
      throw new UserNotAuthenticatedException();
    }
    log.info("Fetching all notifications for login={}", login);
    return recipientRepository
        .findByLogin(login)
        .map(
            recipient ->
                notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient).stream()
                    .map(n -> new NotificationResponseDto(n.getMessage(), n.getCreatedAt()))
                    .toList())
        .orElseGet(
            () -> {
              log.debug("No notifications found for login={}", login);
              return List.of();
            });
  }

  @Override
  public void updateEmailSubscription(EmailSubscriptionDto dto) {
    log.info("Updating email subscription for login={}, enabled={}", dto.login(), dto.enabled());

    RecipientEntity recipientEntity =
        recipientRepository
            .findByLogin(dto.login())
            .orElseGet(
                () -> {
                  log.debug("Recipient not found, creating new: login={}", dto.login());
                  RecipientEntity newRecipient = new RecipientEntity();
                  newRecipient.setLogin(dto.login());
                  return newRecipient;
                });

    recipientEntity.setEmail(dto.email());
    recipientEntity.setEmailNotificationsEnabled(dto.enabled());
    recipientRepository.save(recipientEntity);

    if (dto.enabled()) {
      DeviceSubscriptionEntity sub =
          subscriptionRepository
              .findByLoginAndChannelType(dto.login(), ChannelType.EMAIL)
              .orElse(new DeviceSubscriptionEntity());
      sub.setLogin(dto.login());
      sub.setChannelType(ChannelType.EMAIL);
      sub.setEndpoint(dto.email());
      subscriptionRepository.save(sub);
      log.debug("Email subscription saved for login={}", dto.login());
    } else {
      subscriptionRepository
          .findByLoginAndChannelType(dto.login(), ChannelType.EMAIL)
          .ifPresent(
              sub -> {
                subscriptionRepository.delete(sub);
                log.debug("Email subscription removed for login={}", dto.login());
              });
    }
  }

  @Override
  public void subscribe(DeviceSubscriptionDto dto) {
    String login = securityUtils.getUserLogin();
    if (login == null) {
      log.warn("subscribe called by unauthenticated user");
      throw new UserNotAuthenticatedException();
    }
    log.info("Subscribing login={} to channel={}", login, dto.channelType());

    if (subscriptionRepository.findByLoginAndChannelType(login, dto.channelType()).isPresent()) {
      log.warn("Subscription already exists for login={}, channel={}", login, dto.channelType());
      throw new SubscriptionAlreadyExistsException(login, dto.channelType());
    }

    DeviceSubscriptionEntity subscription = new DeviceSubscriptionEntity();
    subscription.setLogin(login);
    subscription.setChannelType(dto.channelType());
    subscription.setEndpoint(dto.endpoint());
    subscription.setDeviceToken(dto.deviceToken());
    subscriptionRepository.save(subscription);
    log.info("Subscription created for login={}, channel={}", login, dto.channelType());
  }

  @Override
  public void unsubscribe(ChannelType channelType) {
    String login = securityUtils.getUserLogin();
    if (login == null) {
      log.warn("unsubscribe called by unauthenticated user");
      throw new UserNotAuthenticatedException();
    }
    log.info("Unsubscribing login={} from channel={}", login, channelType);

    subscriptionRepository
        .findByLoginAndChannelType(login, channelType)
        .ifPresentOrElse(
            sub -> {
              subscriptionRepository.delete(sub);
              log.info("Subscription removed for login={}, channel={}", login, channelType);
            },
            () -> {
              log.warn("Subscription not found for login={}, channel={}", login, channelType);
              throw new SubscriptionNotFoundException(login, channelType);
            });
  }
}
