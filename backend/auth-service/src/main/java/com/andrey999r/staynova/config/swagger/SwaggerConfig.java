package com.andrey999r.staynova.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {


  @Value("${app.swagger.local-url}")
  private String localUrl;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("Auth Service API").version("1.0"))
        .servers(
            List.of(
                new Server().url(localUrl).description("Local")))
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
}
