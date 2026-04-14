package com.andrey999r.staynova.general.exception.custom.notification;

import com.andrey999r.staynova.general.exception.BaseNotificationException;
import org.springframework.http.HttpStatus;

public class NotificationSendFailedException extends BaseNotificationException {

  public NotificationSendFailedException(String channel, Throwable cause) {
    super(
        "Failed to send notification via channel=" + channel + ": " + cause.getMessage(),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
