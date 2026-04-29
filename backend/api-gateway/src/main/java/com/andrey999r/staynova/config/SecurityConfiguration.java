package com.andrey999r.staynova.config;

import com.andrey999r.staynova.filters.JwtFilter;
import com.andrey999r.staynova.properties.CorsProperties;
import com.andrey999r.staynova.properties.PathProperties;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {

  private final JwtFilter jwtFilter;
  private final CorsProperties corsProperties;
  private final PathProperties securityPathProperties;

  private static String[] toArray(List<String> paths) {
    return paths.toArray(String[]::new);
  }

  @Bean
  public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
    http.cors(
            cors ->
                cors.configurationSource(
                    request -> {
                      CorsConfiguration config = new CorsConfiguration();
                      config.setAllowedOrigins(corsProperties.getAllowedOrigins());
                      config.setAllowedMethods(corsProperties.getAllowedMethods());
                      config.setAllowedHeaders(
                          List.of(
                              HttpHeaders.AUTHORIZATION,
                              HttpHeaders.CONTENT_TYPE,
                              HttpHeaders.ACCEPT,
                              "X-Requested-With",
                              "X-User-Login",
                              "X-User-Roles",
                              "X-Recaptcha-Token"));
                      config.setExposedHeaders(List.of(HttpHeaders.AUTHORIZATION));
                      config.setAllowCredentials(true);
                      config.setMaxAge(3600L);
                      return config;
                    }))
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(
            auth ->
                auth.pathMatchers(toArray(securityPathProperties.getAuthenticatedPaths()))
                    .authenticated()
                    .pathMatchers(toArray(securityPathProperties.getPermitAllPaths()))
                    .permitAll()
                    .anyExchange()
                    .authenticated())
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .exceptionHandling(
            spec ->
                spec.authenticationEntryPoint(
                        (exchange, exception) -> {
                          log.warn(
                              "Unauthorized access attempt to: {}",
                              exchange.getRequest().getPath());

                          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                          return exchange.getResponse().setComplete();
                        })
                    .accessDeniedHandler(
                        (exchange, exception) -> {
                          log.warn("Access denied to: {}", exchange.getRequest().getPath());
                          exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                          return exchange.getResponse().setComplete();
                        }))
        .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION);
    return http.build();
  }
}
