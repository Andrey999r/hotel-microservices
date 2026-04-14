package com.andrey999r.staynova.bl;

import com.andrey999r.staynova.api.dto.WebhookDto;
import com.andrey999r.staynova.general.kafka.dto.request.CancelDto;
import com.andrey999r.staynova.general.kafka.dto.request.PayDto;

public interface PaymentService {

  void initiatePayment(PayDto payDto);

  void processWebhook(WebhookDto webhookDto);

  void cancelPayment(CancelDto cancelDto);
}
