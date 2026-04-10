package com.andrey999r.staynova.config;

import java.util.Objects;

import com.andrey999r.staynova.properties.RateLimiterProperties;
import com.andrey999r.staynova.services.CustomRedisRateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfiguration {
  @Bean
  public RateLimiterProperties rateLimiterProperties() {
    return new RateLimiterProperties();
  }

  @Bean
  public KeyResolver ipKeyResolver() {
    return exchange ->
        Mono.just(
            Objects.requireNonNull(exchange.getRequest().getRemoteAddress())
                .getAddress()
                .getHostAddress());
  }

  @Bean
  @Primary
  public RedisRateLimiter redisRateLimiter(RateLimiterProperties props) {
    return new CustomRedisRateLimiter(
        props.getDefaultReplenishRate(), props.getDefaultBurstCapacity());
  }
}
