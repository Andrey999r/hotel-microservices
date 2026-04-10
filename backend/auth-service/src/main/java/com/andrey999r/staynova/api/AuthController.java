package com.andrey999r.staynova.api;

import com.andrey999r.staynova.api.dto.UserRequestDto;
import com.andrey999r.staynova.bl.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Slf4j
public class AuthController {

  private static final String PATH_LOGIN = "/login";
  private static final String PATH_LOGOUT = "/logout";
  private static final String PATH_REGISTER = "/register";

  private final AuthService authService;

  @PostMapping(PATH_LOGIN)
  public ResponseEntity<String> authorization(@RequestBody UserRequestDto authorizationRequest) {
    log.info("Received login request for user: {}", authorizationRequest.login());
    String token = authService.authorization(authorizationRequest);
    log.info("Login successful for user: {}", authorizationRequest.login());
    return ResponseEntity.ok(token);
  }

  @PostMapping(PATH_REGISTER)
  public ResponseEntity<String> registration(
      @RequestBody UserRequestDto registrationRequest,
      @RequestHeader(value = "X-Admin-Secret", required = false) String secret) {
    log.info("Received registration request for user: {}", registrationRequest.login());
    String token = authService.registration(registrationRequest, secret);
    log.info("Registration successful for user: {}", registrationRequest.login());
    return ResponseEntity.status(201).body(token);
  }

  @PreAuthorize("isAuthenticated()")
  @PostMapping(PATH_LOGOUT)
  public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
    log.info("Received logout request");
    authService.logout(authHeader);
    log.info("Logout processed successfully");
    return ResponseEntity.noContent().build();
  }
}
