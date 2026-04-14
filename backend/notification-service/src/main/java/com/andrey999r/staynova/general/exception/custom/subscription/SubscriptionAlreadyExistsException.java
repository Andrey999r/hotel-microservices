package com.andrey999r.staynova.general.exception.custom.subscription;

import com.andrey999r.staynova.common.kafka.enums.ChannelType;
import com.andrey999r.staynova.general.exception.BaseNotificationException;
import org.springframework.http.HttpStatus;

public class SubscriptionAlreadyExistsException extends BaseNotificationException {

  public SubscriptionAlreadyExistsException(String login, ChannelType channelType) {
    super(
        "Subscription already exists for login=" + login + ", channel=" + channelType,
        HttpStatus.CONFLICT);
  }
}
