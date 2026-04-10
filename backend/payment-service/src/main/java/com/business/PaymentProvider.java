package com.business;

import com.provider.PaymentInitRequest;
import com.provider.PaymentInitResponse;

public interface PaymentProvider {

  PaymentInitResponse initiate(PaymentInitRequest request);

  default boolean isAutoConfirm() {
    return false;
  }
}
