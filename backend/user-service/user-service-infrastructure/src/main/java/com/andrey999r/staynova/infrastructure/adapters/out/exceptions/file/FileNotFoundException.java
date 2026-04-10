package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.file;

import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class FileNotFoundException extends InfrastructureException {
  public FileNotFoundException() {
    super("File not found", HttpStatus.NOT_FOUND);
  }
}
