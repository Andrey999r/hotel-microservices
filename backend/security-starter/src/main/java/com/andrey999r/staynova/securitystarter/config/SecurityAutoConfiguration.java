package com.andrey999r.staynova.securitystarter.config;

import com.andrey999r.staynova.securitystarter.filter.RoleHeaderFilter;
import com.andrey999r.staynova.securitystarter.properties.*;
import com.andrey999r.staynova.securitystarter.properties.FilterProperties;
import com.andrey999r.staynova.securitystarter.services.ContextService;
import com.andrey999r.staynova.securitystarter.services.impl.ContextServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AutoConfiguration
@EnableConfigurationProperties({
  FilterProperties.class,
})
public class SecurityAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  @ConditionalOnMissingBean
  public ContextService contextService() {
    return new ContextServiceImpl();
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  static class ServletSecurityConfig {

    @Bean
    @ConditionalOnMissingBean(RoleHeaderFilter.class)
    @ConditionalOnProperty(
        prefix = "staynova.filters.role-header",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
    public RoleHeaderFilter roleHeaderFilter() {
      return new RoleHeaderFilter();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain servletSecurityFilterChain(
        HttpSecurity http, RoleHeaderFilter roleHeaderFilter) throws Exception {
      http.csrf(AbstractHttpConfigurer::disable)
          .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
          .addFilterBefore(roleHeaderFilter, UsernamePasswordAuthenticationFilter.class);
      return http.build();
    }
  }
}
