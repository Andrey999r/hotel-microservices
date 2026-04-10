package com.andrey999r.staynova.domain.vo.user;

import com.andrey999r.staynova.domain.exceptions.user.BlankPicturePathException;
import com.andrey999r.staynova.domain.exceptions.user.NullPicturePathException;

public record PicturePath(String path) {

  public PicturePath {
    ensureNotNull(path);
    ensureNotBlank(path);
  }

  private static void ensureNotNull(String value) {
    if (value == null) {
      throw new NullPicturePathException();
    }
  }

  private static void ensureNotBlank(String value) {
    if (value.isBlank()) {
      throw new BlankPicturePathException();
    }
  }
}
