package com.andrey999r.staynova.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.cors")
@Component
public class CorsProperties {

  private List<String> allowedOrigins;
  private List<String> allowedMethods;
}
