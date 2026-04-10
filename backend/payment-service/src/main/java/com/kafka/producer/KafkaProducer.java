package com.kafka.producer;

import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import com.business.PaymentResultPublisher;
import com.kafka.dto.response.PaymentResultKafkaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer implements PaymentResultPublisher {

  private final KafkaTemplate<String, PaymentResultKafkaDto> paymentResultKafkaTemplate;

  @Override
  public void sendPaymentUrl(long reservationId, String paymentUrl) {
    PaymentResultKafkaDto dto = new PaymentResultKafkaDto(reservationId, paymentUrl, false, null);
    paymentResultKafkaTemplate.send(KafkaTopics.PAYMENT_RESULT_TOPIC, dto);
    log.info("Sent payment URL for reservationId={}", reservationId);
  }

  @Override
  public void sendPaymentResult(long reservationId, boolean success, String errorMessage) {
    PaymentResultKafkaDto dto =
        new PaymentResultKafkaDto(reservationId, null, success, errorMessage);
    paymentResultKafkaTemplate.send(KafkaTopics.PAYMENT_RESULT_TOPIC, dto);
    log.info("Sent payment result for reservationId={}, success={}", reservationId, success);
  }
}
