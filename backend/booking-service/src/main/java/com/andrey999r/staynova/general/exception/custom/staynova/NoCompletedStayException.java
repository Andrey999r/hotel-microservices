package com.andrey999r.staynova.general.exception.custom.staynova;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NoCompletedStayException extends BaseException {
  public NoCompletedStayException() {
    super(
        "Оставить отзыв могут только гости, у которых есть завершённое подтверждённое проживание в данном отеле.",
        HttpStatus.FORBIDDEN);
  }
}
