package com.andrey999r.staynova.bl.client;

import com.andrey999r.staynova.api.dto.UserDetailsDto;
import com.andrey999r.staynova.api.dto.UserRequestDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface UserServiceClient {

  @GetExchange("/internal/users")
  UserDetailsDto getDetails(@RequestParam("login") String login);

  @PostExchange("/internal/users")
  void createUser(
      @RequestBody UserRequestDto userRequestDto,
      @RequestHeader(value = "X-Admin-Secret", required = false) String secret);
}
