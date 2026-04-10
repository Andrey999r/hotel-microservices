package com.andrey999r.staynova.bl;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlacklistService implements TokenBlacklistService {

  private static final String BLACKLIST_PREFIX = "blacklist:";

  private final RedisTemplate<String, String> redisTemplate;

  @Override
  public void blacklistToken(String token, long expirationMls) {
    String key = BLACKLIST_PREFIX + token;
    redisTemplate.opsForValue().set(key, "true", expirationMls, TimeUnit.MILLISECONDS);
    log.debug("Token added to blacklist, key={}", key);
  }
}
