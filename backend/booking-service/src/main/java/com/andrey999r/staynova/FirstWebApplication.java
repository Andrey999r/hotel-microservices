package com.andrey999r.staynova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableScheduling
@SpringBootApplication
public class FirstWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(FirstWebApplication.class, args);
  }
}
