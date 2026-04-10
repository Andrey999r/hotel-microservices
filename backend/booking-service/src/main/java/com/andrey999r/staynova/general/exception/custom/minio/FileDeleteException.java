package com.andrey999r.staynova.general.exception.custom.minio;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileDeleteException extends BaseException {
  public FileDeleteException(String fileUrl, Throwable cause) {
    super("Failed to delete file: " + fileUrl, HttpStatus.INTERNAL_SERVER_ERROR, cause);
  }
}
