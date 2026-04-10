package com.andrey999r.staynova.bl.impls;

import com.andrey999r.staynova.bl.NotificationSender;
import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.dal.entities.DeviceSubscriptionEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WebPushSenderImpl implements NotificationSender {
  @Override
  public void send(DeviceSubscriptionEntity subscription, String message) {
    log.info("Sending WEB push to {}: {}", subscription.getLogin(), message);
  }

  @Override
  public ChannelType supports() {
    return ChannelType.WEB;
  }
}
