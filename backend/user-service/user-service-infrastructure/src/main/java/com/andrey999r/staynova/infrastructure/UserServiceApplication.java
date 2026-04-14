package com.andrey999r.staynova.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@EnableJpaRepositories(
    basePackages = "com.andrey999r.staynova.infrastructure.adapters.out.jpa.interfaces")
public class UserServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }
}
