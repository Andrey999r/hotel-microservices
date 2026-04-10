package com.provider;

import java.util.UUID;

import com.business.PaymentProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StubPaymentProvider implements PaymentProvider {

  private static final String STUB_PAYMENT_BASE_URL = "http://localhost:5173/payment/";

  @Override
  public PaymentInitResponse initiate(PaymentInitRequest request) {
    String sessionId = UUID.randomUUID().toString();
    String paymentUrl =
        STUB_PAYMENT_BASE_URL + sessionId + "?reservationId=" + request.reservationId();
    log.info(
        "Stub: created payment session={} for reservationId={}, user={}",
        sessionId,
        request.reservationId(),
        request.userLogin());
    return new PaymentInitResponse(sessionId, paymentUrl);
  }

  @Override
  public boolean isAutoConfirm() {
    return true;
  }
}
