package com.andrey999r.staynova.infrastructure.adapters.out.config;

import com.andrey999r.staynova.domain.ports.out.AdminSecretOutputPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminSecretOutputAdapter implements AdminSecretOutputPort {

  @Value("${admin.secret}")
  private String adminSecret;

  @Override
  public String getAdminSecret() {
    return adminSecret;
  }
}
