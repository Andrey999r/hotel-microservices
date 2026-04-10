package com.business;

import com.api.dto.WebhookDto;
import com.kafka.dto.request.CancelDto;
import com.kafka.dto.request.PayDto;

public interface PaymentService {

  void initiatePayment(PayDto payDto);

  void processWebhook(WebhookDto webhookDto);

  void cancelPayment(CancelDto cancelDto);
}
