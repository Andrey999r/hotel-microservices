package com.andrey999r.staynova.general.exception.custom.staynova;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class HotelNotFoundException extends BaseException {
  public HotelNotFoundException(Long id) {
    super("Hotel not found: id=" + id, HttpStatus.NOT_FOUND);
  }
}
