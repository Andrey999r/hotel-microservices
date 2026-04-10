package com.andrey999r.staynova.exception.custom;

import com.andrey999r.staynova.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RateLimitExceededException extends BaseException {
  public RateLimitExceededException() {
    super("Rate limit exceeded. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
  }
}
