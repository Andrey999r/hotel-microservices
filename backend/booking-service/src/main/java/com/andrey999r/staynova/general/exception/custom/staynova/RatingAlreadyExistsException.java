package com.andrey999r.staynova.general.exception.custom.staynova;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RatingAlreadyExistsException extends BaseException {
  public RatingAlreadyExistsException(String login, Long hotelId) {
    super("User '" + login + "' has already rated hotel id=" + hotelId, HttpStatus.CONFLICT);
  }
}
