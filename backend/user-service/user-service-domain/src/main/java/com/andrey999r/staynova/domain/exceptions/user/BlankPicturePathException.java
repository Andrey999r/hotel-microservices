package com.andrey999r.staynova.domain.exceptions.user;

import com.andrey999r.staynova.domain.exceptions.DomainException;

public class BlankPicturePathException extends DomainException {
  public BlankPicturePathException() {
    super("Picture path is blank");
  }
}
