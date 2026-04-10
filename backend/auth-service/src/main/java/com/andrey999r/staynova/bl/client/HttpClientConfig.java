package com.andrey999r.staynova.bl.client;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@EnableConfigurationProperties(UserServiceProperties.class)
public class HttpClientConfig {

  @Bean
  public UserServiceClient userServiceClient(UserServiceProperties properties) {
    RestClient restClient = RestClient.builder().baseUrl(properties.getUrl()).build();
    HttpServiceProxyFactory factory =
        HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build();
    return factory.createClient(UserServiceClient.class);
  }
}
