package com.andrey999r.staynova.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

  @Value("${app.swagger.local-url}")
  private String localUrl;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("User Service API").version("1.0"))
        .servers(List.of(new Server().url(localUrl).description("Local")))
        .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Bearer Token",
                    new SecurityScheme()
                        .name("Bearer Token")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Bean
  public OperationCustomizer hideGatewayHeaders() {
    return (operation, handlerMethod) -> {
      if (operation.getParameters() != null) {
        operation
            .getParameters()
            .removeIf(
                param ->
                    param.getIn().equals("header")
                        && (param.getName().equals("X-User-RoleType")
                            || param.getName().equals("X-User-Login")));
      }
      return operation;
    };
  }
}
