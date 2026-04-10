package com.andrey999r.staynova.infrastructure.adapters.in.web.restcontrollers;

import com.andrey999r.staynova.domain.ports.in.commands.RegisterUserInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.GetUserInfoInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.GetUserInfoQuery;
import com.andrey999r.staynova.domain.ports.in.results.UserInfoResult;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.RegisterUserRequestDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.UserInfoResponseDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.mappers.InternalWebMapper;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/internal")
@Hidden
public class InternalController {

  private final GetUserInfoInputPort getUserInfoUseCase;
  private final RegisterUserInputPort registerUserUseCase;
  private final InternalWebMapper internalWebMapper;

  @GetMapping("/users")
  public ResponseEntity<UserInfoResponseDto> getUserDetails(@RequestParam @NotNull String login) {
    log.info("GET /internal/users login={}", login);
    UserInfoResult result = getUserInfoUseCase.execute(new GetUserInfoQuery(new Login(login)));
    log.info("GET /internal/users → found user login={}", login);
    return ResponseEntity.ok(internalWebMapper.toResponseDto(result));
  }

  @PostMapping("/users")
  public ResponseEntity<Void> createUser(
      @RequestBody RegisterUserRequestDto request,
      @RequestHeader(value = "X-Admin-Secret", required = false) String secret) {
    log.info("POST /internal/users login={}", request.login());
    registerUserUseCase.execute(internalWebMapper.toCommand(request, secret));
    log.info("POST /internal/users → created login={}", request.login());
    return ResponseEntity.status(201).build();
  }
}
