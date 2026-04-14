package com.andrey999r.staynova.bl;

import com.andrey999r.staynova.api.dto.PaymentInitRequest;
import com.andrey999r.staynova.api.dto.PaymentInitResponse;

public interface PaymentProvider {

  PaymentInitResponse initiate(PaymentInitRequest request);

  default boolean isAutoConfirm() {
    return false;
  }
}
