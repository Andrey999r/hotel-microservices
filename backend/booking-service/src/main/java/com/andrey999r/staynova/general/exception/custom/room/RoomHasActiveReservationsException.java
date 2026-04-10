package com.andrey999r.staynova.general.exception.custom.room;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoomHasActiveReservationsException extends BaseException {
  public RoomHasActiveReservationsException(long roomId, long count) {
    super(
        "Cannot delete room "
            + roomId
            + ": has "
            + count
            + " active reservations. Cancel them first.",
        HttpStatus.CONFLICT);
  }
}
