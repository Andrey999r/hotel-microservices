package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class NullPicturePathException extends DomainException {
  public NullPicturePathException() {
    super("Picture path is null");
  }
}
