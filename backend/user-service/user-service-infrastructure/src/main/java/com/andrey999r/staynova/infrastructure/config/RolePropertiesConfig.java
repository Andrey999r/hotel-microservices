package com.andrey999r.staynova.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RolePropertiesConfig {

  @Bean
  @ConfigurationProperties(prefix = "role-names")
  public RoleConfig roleConfig() {
    return new RoleConfig();
  }
}
