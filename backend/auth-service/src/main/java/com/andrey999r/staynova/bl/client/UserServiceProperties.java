package com.andrey999r.staynova.bl.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "clients.user-service")
public class UserServiceProperties {
  private String url;
}
