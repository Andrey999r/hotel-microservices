package com.andrey999r.staynova.general.exception.custom.staynova;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidSearchQueryException extends BaseException {
  public InvalidSearchQueryException() {
    super("Search query must be between 1 and 200 characters", HttpStatus.BAD_REQUEST);
  }
}
