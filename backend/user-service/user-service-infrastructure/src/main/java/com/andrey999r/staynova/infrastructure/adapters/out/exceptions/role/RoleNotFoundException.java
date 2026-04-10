package com.andrey999r.staynova.infrastructure.adapters.out.exceptions.role;

import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.InfrastructureException;
import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends InfrastructureException {
  public RoleNotFoundException(String role) {
    super("Role not found: " + role, HttpStatus.NOT_FOUND);
  }
}
