package com.andrey999r.staynova.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class BlacklistService {
  private static final String BLACKLIST_PREFIX = "blacklist:";

  private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

  public Mono<Boolean> isBlacklisted(String token) {
    String key = BLACKLIST_PREFIX + token;
    return reactiveRedisTemplate
        .hasKey(key)
        .doOnNext(
            blacklisted -> {
              if (blacklisted) {
                log.debug("Token found in blacklist, key={}", key);
              }
            });
  }
}
