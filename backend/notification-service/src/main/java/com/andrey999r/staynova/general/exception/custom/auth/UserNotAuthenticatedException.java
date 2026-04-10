package com.andrey999r.staynova.general.exception.custom.auth;

import com.andrey999r.staynova.general.exception.BaseNotificationException;
import org.springframework.http.HttpStatus;

public class UserNotAuthenticatedException extends BaseNotificationException {

  public UserNotAuthenticatedException() {
    super("User is not authenticated", HttpStatus.UNAUTHORIZED);
  }
}
