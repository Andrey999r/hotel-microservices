package com.andrey999r.staynova.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "recaptcha")
public class ReCaptchaProperties {

  private String secret;
  private String url;
  private double scoreThreshold;
}
