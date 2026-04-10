package com.andrey999r.staynova.general.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

  @Value("${spring.data.redis.host}")
  private String host;

  @Value("${spring.data.redis.port}")
  private int port;

  @Value("${spring.data.redis.password}")
  private String password;

  @Bean("popularityConnectionFactory")
  public RedisConnectionFactory popularityConnectionFactory() {
    return buildFactory(1);
  }

  @Bean("dedupConnectionFactory")
  public RedisConnectionFactory dedupConnectionFactory() {
    return buildFactory(2);
  }

  private RedisConnectionFactory buildFactory(int database) {
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
    config.setHostName(host);
    config.setPort(port);
    config.setPassword(password);
    config.setDatabase(database);
    return new LettuceConnectionFactory(config);
  }

  @Bean("popularityRedisTemplate")
  public RedisTemplate<String, String> popularityRedisTemplate(
      @Qualifier("popularityConnectionFactory") RedisConnectionFactory factory) {
    RedisTemplate<String, String> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setHashValueSerializer(new StringRedisSerializer());
    return template;
  }

  @Bean("stringRedisTemplate")
  public StringRedisTemplate stringRedisTemplate(
      @Qualifier("dedupConnectionFactory") RedisConnectionFactory factory) {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(factory);
    return template;
  }

  @Bean("redisTemplate")
  public RedisTemplate<Object, Object> redisTemplate(
      @Qualifier("dedupConnectionFactory") RedisConnectionFactory factory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(factory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    return template;
  }
}
