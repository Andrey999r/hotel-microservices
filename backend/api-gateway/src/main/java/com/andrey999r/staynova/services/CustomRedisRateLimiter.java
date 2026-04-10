package com.andrey999r.staynova.services;

import com.andrey999r.staynova.exception.custom.RateLimitExceededException;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import reactor.core.publisher.Mono;

public class CustomRedisRateLimiter extends RedisRateLimiter {
  public CustomRedisRateLimiter(int defaultReplenishRate, long defaultBurstCapacity) {
    super(defaultReplenishRate, defaultBurstCapacity);
  }

  @Override
  public Mono<Response> isAllowed(String routeId, String id) {
    return super.isAllowed(routeId, id)
        .flatMap(
            response -> {
              if (response.isAllowed()) {
                return Mono.just(response);
              } else {
                return Mono.error(new RateLimitExceededException());
              }
            });
  }
}
