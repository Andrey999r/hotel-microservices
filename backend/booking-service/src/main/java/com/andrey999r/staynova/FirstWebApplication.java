package com.andrey999r.staynova;

import com.andrey999r.staynova.general.minio.MinioProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableConfigurationProperties(MinioProperties.class)
@EnableScheduling
@SpringBootApplication
public class FirstWebApplication {

  public static void main(String[] args) {
    SpringApplication.run(FirstWebApplication.class, args);
  }
}
