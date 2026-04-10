package com.andrey999r.staynova.bl.impls;

import com.andrey999r.staynova.bl.NotificationSender;
import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.dal.entities.DeviceSubscriptionEntity;
import com.andrey999r.staynova.general.exception.custom.notification.NotificationSendFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailSenderImpl implements NotificationSender {

  private final JavaMailSender mailSender;

  @Override
  public void send(DeviceSubscriptionEntity subscription, String message) {
    String to = subscription.getEndpoint();
    log.info("Sending EMAIL notification to={}", to);
    try {
      SimpleMailMessage mailMessage = new SimpleMailMessage();
      mailMessage.setTo(to);
      mailMessage.setSubject("Hotel Kust notification");
      mailMessage.setText(message);
      mailSender.send(mailMessage);
      log.info("EMAIL notification sent successfully to={}", to);
    } catch (Exception e) {
      log.error("Failed to send EMAIL notification to={}: {}", to, e.getMessage(), e);
      throw new NotificationSendFailedException(ChannelType.EMAIL.name(), e);
    }
  }

  @Override
  public ChannelType supports() {
    return ChannelType.EMAIL;
  }
}
