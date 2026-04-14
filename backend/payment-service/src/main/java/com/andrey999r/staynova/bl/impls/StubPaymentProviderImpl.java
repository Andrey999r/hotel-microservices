package com.andrey999r.staynova.bl.impls;

import java.util.UUID;

import com.andrey999r.staynova.bl.PaymentProvider;
import com.andrey999r.staynova.api.dto.PaymentInitRequest;
import com.andrey999r.staynova.api.dto.PaymentInitResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StubPaymentProviderImpl implements PaymentProvider {

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
