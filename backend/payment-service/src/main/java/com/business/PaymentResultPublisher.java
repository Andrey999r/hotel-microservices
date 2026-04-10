package com.business;

public interface PaymentResultPublisher {

  void sendPaymentUrl(long reservationId, String paymentUrl);

  void sendPaymentResult(long reservationId, boolean success, String errorMessage);
}
