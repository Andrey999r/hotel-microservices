package com.andrey999r.staynova.infrastructure.adapters.in.web.mappers;

import com.andrey999r.staynova.domain.ports.in.commands.UpdateProfileCommand;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;
import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.PicturePath;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.ProfileInfoResponseDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.UpdateProfileRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

  @Mapping(target = "login", expression = "java(mapLogin(result.login()))")
  @Mapping(target = "email", expression = "java(mapEmail(result.email()))")
  @Mapping(target = "avatarUrl", expression = "java(mapPicturePath(result.picturePath()))")
  ProfileInfoResponseDto toResponseDto(ProfileInfoResult result);

  @Mapping(target = "email", expression = "java(mapEmail(request.email()))")
  @Mapping(target = "emailSubscriptionStatus", expression = "java(mapBooleanFlag(request.emailNotificationsEnabled()))")
  UpdateProfileCommand toCommand(UpdateProfileRequestDto request);

  default String mapLogin(Login login) {
    return login == null ? null : login.login();
  }

  default String mapEmail(Email email) {
    return email == null ? null : email.email();
  }

  default String mapPicturePath(PicturePath path) {
    return path == null ? null : path.path();
  }

  default Email mapEmail(String email) {
    return email == null ? null : new Email(email);
  }

  default boolean mapBooleanFlag(Boolean flag) {
    return flag != null && flag;
  }
}
