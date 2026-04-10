package com.andrey999r.staynova.general.kafka.payment;

import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import com.andrey999r.staynova.general.kafka.payment.dto.CancelPaymentKafkaDto;
import com.andrey999r.staynova.general.kafka.payment.dto.PaymentRequestKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestProducer implements PaymentEventPublisher {

  private final KafkaTemplate<String, PaymentRequestKafkaDto> paymentRequestKafkaTemplate;
  private final KafkaTemplate<String, CancelPaymentKafkaDto> cancelPaymentKafkaTemplate;

  @Override
  public void sendPaymentRequest(PaymentRequestKafkaDto dto) {
    paymentRequestKafkaTemplate.send(KafkaTopics.PAYMENT_REQUEST_TOPIC, dto);
    log.info(
        "Sent payment request: reservationId={}, user={}, roomId={}",
        dto.reservationId(),
        dto.userLogin(),
        dto.roomId());
  }

  @Override
  public void sendCancelRequest(CancelPaymentKafkaDto dto) {
    cancelPaymentKafkaTemplate.send(KafkaTopics.CANCEL_REQUEST_TOPIC, dto);
    log.info(
        "Sent cancel payment request: reservationId={}, user={}",
        dto.reservationId(),
        dto.userLogin());
  }
}
