package com.andrey999r.staynova.infrastructure.adapters.out.jpa.mappers;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.Password;
import com.andrey999r.staynova.domain.vo.user.PicturePath;
import com.andrey999r.staynova.domain.vo.user.UserId;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities.UserEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

  @Mapping(target = "id", expression = "java(mapUserId(userEntity.getId()))")
  @Mapping(target = "login", expression = "java(new com.andrey999r.staynova.domain.vo.user.Login(userEntity.getLogin()))")
  @Mapping(target = "password", expression = "java(new com.andrey999r.staynova.domain.vo.user.Password(userEntity.getPassword()))")
  @Mapping(target = "email", expression = "java(new com.andrey999r.staynova.domain.vo.user.Email(userEntity.getEmail()))")
  @Mapping(target = "picturePath", expression = "java(new com.andrey999r.staynova.domain.vo.user.PicturePath(userEntity.getPicturePath()))")
  @Mapping(target = "emailSubscriptionStatus", source = "emailSubscriptionEnabled")
  User toModel(UserEntity userEntity);

  @Mapping(target = "id", expression = "java(mapUUID(user.getId()))")
  @Mapping(target = "login", expression = "java(user.getLogin().login())")
  @Mapping(target = "password", expression = "java(user.getPassword().password())")
  @Mapping(target = "email", expression = "java(user.getEmail().email())")
  @Mapping(target = "picturePath", expression = "java(user.getPicturePath().path())")
  @Mapping(target = "emailSubscriptionEnabled", source = "emailSubscriptionStatus")
  UserEntity toEntity(User user);

  default UserId mapUserId(UUID id) {
    return id == null ? null : new UserId(id);
  }

  default UUID mapUUID(UserId userId) {
    return userId == null ? null : userId.id();
  }
}
