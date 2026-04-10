package com.andrey999r.staynova.infrastructure.adapters.out.kafka.mappers;

import com.andrey999r.staynova.common.kafka.dto.EmailSubscriptionDto;
import com.andrey999r.staynova.domain.ports.out.requests.EmailSubscriptionRequest;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EmailSubscriptionKafkaMapper {

  @Mapping(target = "login", expression = "java(map(emailSubscriptionRequest.login()))")
  @Mapping(target = "email", expression = "java(map(emailSubscriptionRequest.email()))")
  EmailSubscriptionDto toDto(EmailSubscriptionRequest emailSubscriptionRequest);

  default String map(Login login) {
    return login == null ? null : login.login();
  }

  default String map(Email email) {
    return email == null ? null : email.email();
  }
}
