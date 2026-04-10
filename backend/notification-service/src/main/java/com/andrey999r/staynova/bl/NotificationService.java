package com.andrey999r.staynova.bl;


import com.andrey999r.staynova.api.dto.NotificationResponseDto;
import com.andrey999r.staynova.common.kafka.dto.DeviceSubscriptionDto;
import com.andrey999r.staynova.common.kafka.dto.EmailSubscriptionDto;
import com.andrey999r.staynova.common.kafka.dto.NotificationKafkaDto;
import com.andrey999r.staynova.common.kafka.enums.ChannelType;

import java.util.List;

public interface NotificationService {
  void publishNotification(NotificationKafkaDto dto);

  List<NotificationResponseDto> getAllNotifications();

  void updateEmailSubscription(EmailSubscriptionDto dto);

  void subscribe(DeviceSubscriptionDto dto);

  void unsubscribe(ChannelType channelType);
}
