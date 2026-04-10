package com.andrey999r.staynova.general.exception.custom.staynova;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class HotelHasActiveRoomsException extends BaseException {
  public HotelHasActiveRoomsException(Long hotelId, long count) {
    super(
        "Cannot delete hotel id=" + hotelId + ": " + count + " active reservation(s) exist",
        HttpStatus.CONFLICT);
  }
}
