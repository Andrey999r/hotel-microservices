package com.andrey999r.staynova.config.jwt;

import java.time.Duration;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtProperties {
  String secret;
  Duration expiration;
}
