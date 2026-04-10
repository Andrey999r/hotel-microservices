package com.andrey999r.staynova.infrastructure.adapters.in.web.mappers;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.in.commands.AddRoleToUserCommand;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.AddRoleToUserRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdminWebMapper {

  @Mapping(target = "login", expression = "java(new com.andrey999r.staynova.domain.vo.user.Login(request.login()))")
  @Mapping(target = "roleName", expression = "java(new com.andrey999r.staynova.domain.vo.role.RoleName(request.roleName()))")
  AddRoleToUserCommand toCommand(AddRoleToUserRequestDto request);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roleName", expression = "java(new com.andrey999r.staynova.domain.vo.role.RoleName(roleName))")
  Role toRole(String roleName);

  default String mapRoleName(Role role) {
    return role.getRoleName() == null ? null : role.getRoleName().name();
  }

  default String mapLogin(Login login) {
    return login == null ? null : login.login();
  }
}
