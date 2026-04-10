package com.andrey999r.staynova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

  @Bean
  public WebClient.Builder webClientBuilder() {
    return WebClient.builder();
  }
}
