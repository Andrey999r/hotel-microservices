package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.ports.out.requests.EmailSubscriptionRequest;

public interface EmailSubscriptionOutputPort {
  void sendSubscriptionInfo(EmailSubscriptionRequest emailSubscriptionInfo);
}
