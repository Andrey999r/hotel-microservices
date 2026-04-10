package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.role;

import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class RoleAlreadyExistsException extends InfrastructureException {
  public RoleAlreadyExistsException(String role) {
    super("Role already exists: " + role, HttpStatus.CONFLICT);
  }
}
