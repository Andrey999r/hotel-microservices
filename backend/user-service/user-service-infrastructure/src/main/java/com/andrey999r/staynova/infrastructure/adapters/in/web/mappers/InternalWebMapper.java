package com.andrey999r.staynova.infrastructure.adapters.in.web.mappers;

import com.andrey999r.staynova.domain.ports.in.commands.RegisterUserCommand;
import com.andrey999r.staynova.domain.ports.in.results.UserInfoResult;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.Password;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.RegisterUserRequestDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.UserInfoResponseDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InternalWebMapper {

  @Mapping(target = "login", expression = "java(mapLogin(result.login()))")
  @Mapping(target = "password", expression = "java(mapPassword(result.password()))")
  @Mapping(target = "roles", expression = "java(mapRoleNames(result.roleNames()))")
  UserInfoResponseDto toResponseDto(UserInfoResult result);

  @Mapping(target = "login", expression = "java(new com.andrey999r.staynova.domain.vo.user.Login(request.login()))")
  @Mapping(target = "email", expression = "java(new com.andrey999r.staynova.domain.vo.user.Email(request.email()))")
  @Mapping(target = "password", expression = "java(new com.andrey999r.staynova.domain.vo.user.Password(request.password()))")
  @Mapping(target = "adminSecret", source = "adminSecret")
  RegisterUserCommand toCommand(RegisterUserRequestDto request, String adminSecret);

  default String mapLogin(Login login) {
    return login == null ? null : login.login();
  }

  default String mapPassword(Password password) {
    return password == null ? null : password.password();
  }

  default List<String> mapRoleNames(List<RoleName> roleNames) {
    if (roleNames == null) {
      return List.of();
    }
    return roleNames.stream().map(RoleName::name).toList();
  }
}
