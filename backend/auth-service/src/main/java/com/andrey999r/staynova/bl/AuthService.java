package com.andrey999r.staynova.bl;

import com.andrey999r.staynova.api.dto.UserRequestDto;

public interface AuthService {
  void logout(String authHeader);

  String registration(UserRequestDto request, String secret);

  String authorization(UserRequestDto userRequestDto);
}
