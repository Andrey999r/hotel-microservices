package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.role;

import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class UnsupportedRoleException extends InfrastructureException {
  public UnsupportedRoleException(String role) {
    super("Unsupported role: " + role, HttpStatus.BAD_REQUEST);
  }
}
