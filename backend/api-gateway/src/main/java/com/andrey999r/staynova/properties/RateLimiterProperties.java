package com.andrey999r.staynova.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "spring.cloud.gateway.rate-limiter")
@Configuration
@Getter
@Setter
public class RateLimiterProperties {
  private int defaultReplenishRate = 10;
  private int defaultBurstCapacity = 20;
}
