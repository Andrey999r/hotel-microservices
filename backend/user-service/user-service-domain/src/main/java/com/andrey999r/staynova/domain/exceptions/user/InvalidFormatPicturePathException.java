package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class InvalidFormatPicturePathException extends DomainException {
  public InvalidFormatPicturePathException() {
    super("Invalid format picture path");
  }
}
