package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.ports.out.requests.NotificationSendRequest;

public interface NotificationOutputPort {
  void sendNotificationInfo(NotificationSendRequest notificationInfo);
}
