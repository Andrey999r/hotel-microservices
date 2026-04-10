package com.andrey999r.staynova.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  @Value("${spring.data.redis.password:}")
  private String redisPassword;

  @Bean
  public RedisConnectionFactory dedupRedisConnectionFactory() {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
    config.setDatabase(3);
    if (redisPassword != null && !redisPassword.isBlank()) {
      config.setPassword(redisPassword);
    }
    return new LettuceConnectionFactory(config);
  }

  @Bean
  public StringRedisTemplate stringRedisTemplate() {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(dedupRedisConnectionFactory());
    return template;
  }
}
