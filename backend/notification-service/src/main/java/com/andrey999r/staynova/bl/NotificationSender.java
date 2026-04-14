package com.andrey999r.staynova.bl;

import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.dal.entities.DeviceSubscriptionEntity;

public interface NotificationSender {
  void send(DeviceSubscriptionEntity subscription, String message);

  ChannelType supports();
}
