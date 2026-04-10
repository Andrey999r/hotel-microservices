package com.andrey999r.staynova.securitystarter.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "staynova.filters")
public class FilterProperties {
  private boolean roleHeader = false;
}
