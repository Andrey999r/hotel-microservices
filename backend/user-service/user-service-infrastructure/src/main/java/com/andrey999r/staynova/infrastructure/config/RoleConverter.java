package com.andrey999r.staynova.infrastructure.config;

import com.andrey999r.staynova.domain.vo.role.RoleName;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConfigurationPropertiesBinding
public class RoleConverter implements Converter<String, RoleName> {

  @Override
  public RoleName convert(@NonNull String value) {
    return new RoleName(value);
  }
}
