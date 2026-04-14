package com.andrey999r.staynova.general.exceptions.custom.kafka;

import com.andrey999r.staynova.general.exceptions.BaseException;
import org.springframework.http.HttpStatus;

public class PayDtoSerializingException extends BaseException {
  public PayDtoSerializingException(String topic) {
    super("Error serializing payDto with topic " + topic, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
