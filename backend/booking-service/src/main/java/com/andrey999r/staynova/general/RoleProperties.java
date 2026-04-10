package com.andrey999r.staynova.general;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "roles")
public class RoleProperties {
  private String adminRole;
  private String userRole;
}
