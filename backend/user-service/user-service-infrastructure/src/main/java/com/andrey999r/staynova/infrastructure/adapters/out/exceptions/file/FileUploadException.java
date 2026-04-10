package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.file;

import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class FileUploadException extends InfrastructureException {
  public FileUploadException(String fileName) {
    super("Failed to upload file: " + fileName, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
