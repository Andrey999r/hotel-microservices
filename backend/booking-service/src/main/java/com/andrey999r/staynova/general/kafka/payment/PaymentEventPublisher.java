package com.andrey999r.staynova.general.kafka.payment;

import com.andrey999r.staynova.general.kafka.payment.dto.CancelPaymentKafkaDto;
import com.andrey999r.staynova.general.kafka.payment.dto.PaymentRequestKafkaDto;

public interface PaymentEventPublisher {

  void sendPaymentRequest(PaymentRequestKafkaDto dto);

  void sendCancelRequest(CancelPaymentKafkaDto dto);
}
