package com.kafka.listeners;

import com.andrey999r.staynova.common.kafka.topics.KafkaTopics;
import com.business.PaymentService;
import com.kafka.dto.request.CancelDto;
import com.kafka.dto.request.PayDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaListener {

  private final PaymentService paymentService;

  @KafkaListener(
      topics = KafkaTopics.PAYMENT_REQUEST_TOPIC,
      groupId = "payment-service-group"
  )
  public void handlePaymentRequest(PayDto payDto) {
    log.info(
        "Received payment request: reservationId={}, user={}",
        payDto.reservationId(),
        payDto.userLogin());
    paymentService.initiatePayment(payDto);
  }

  @KafkaListener(
      topics = KafkaTopics.CANCEL_REQUEST_TOPIC,
      groupId = "payment-service-group"
  )
  public void handleCancellationRequest(CancelDto cancelDto) {
    log.info(
        "Received cancellation request: reservationId={}, user={}",
        cancelDto.reservationId(),
        cancelDto.userLogin());
    paymentService.cancelPayment(cancelDto);
  }
}
