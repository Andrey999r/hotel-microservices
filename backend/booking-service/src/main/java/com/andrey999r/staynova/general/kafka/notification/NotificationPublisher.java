package com.andrey999r.staynova.general.kafka.notification;


import com.andrey999r.staynova.common.kafka.enums.NotificationEventType;

public interface NotificationPublisher {

  void send(String login, String message, NotificationEventType eventType);
}
