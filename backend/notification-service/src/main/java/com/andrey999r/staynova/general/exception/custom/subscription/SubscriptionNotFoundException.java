package com.andrey999r.staynova.general.exception.custom.subscription;

import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.general.exception.BaseNotificationException;
import org.springframework.http.HttpStatus;

public class SubscriptionNotFoundException extends BaseNotificationException {

  public SubscriptionNotFoundException(String login, ChannelType channelType) {
    super(
        "Subscription not found for login=" + login + ", channel=" + channelType,
        HttpStatus.NOT_FOUND);
  }
}
