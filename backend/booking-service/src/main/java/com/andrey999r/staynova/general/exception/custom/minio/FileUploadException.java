package com.andrey999r.staynova.general.exception.custom.minio;

import com.andrey999r.staynova.general.exception.BaseException;
import org.springframework.http.HttpStatus;

public class FileUploadException extends BaseException {
  public FileUploadException(String fileName, Throwable cause) {
    super("Failed to upload file: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR, cause);
  }
}
