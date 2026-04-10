package com.andrey999r.staynova.infrastructure.config;

import com.andrey999r.staynova.domain.vo.role.RoleName;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "role-names")
public class RoleConfig {
  private RoleName adminRoleName;
  private RoleName userRoleName;
}
