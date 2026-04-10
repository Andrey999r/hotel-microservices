package com.andrey999r.staynova.infrastructure.adapters.out.jpa.mappers;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.vo.role.RoleId;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities.RoleEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  @Mapping(target = "id", expression = "java(mapRoleId(entity.getId()))")
  @Mapping(target = "roleName", expression = "java(new com.andrey999r.staynova.domain.vo.role.RoleName(entity.getRoleName()))")
  Role toModel(RoleEntity entity);

  @Mapping(target = "id", expression = "java(mapUUID(role.getId()))")
  @Mapping(target = "roleName", expression = "java(role.getRoleName().name())")
  RoleEntity toEntity(Role role);

  default RoleId mapRoleId(UUID id) {
    return id == null ? null : new RoleId(id);
  }

  default UUID mapUUID(RoleId roleId) {
    return roleId == null ? null : roleId.id();
  }
}
