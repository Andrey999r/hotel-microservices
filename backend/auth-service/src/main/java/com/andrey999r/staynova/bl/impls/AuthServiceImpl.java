package com.andrey999r.staynova.bl.impls;

import com.andrey999r.staynova.api.dto.UserDetailsDto;
import com.andrey999r.staynova.api.dto.UserRequestDto;
import com.andrey999r.staynova.bl.AuthService;
import com.andrey999r.staynova.bl.TokenBlacklistService;
import com.andrey999r.staynova.bl.client.UserServiceClient;
import com.andrey999r.staynova.config.jwt.JwtTokenUtils;
import com.andrey999r.staynova.exceptions.custom.auth.InvalidCredentialsException;
import com.andrey999r.staynova.exceptions.custom.auth.InvalidTokenHeaderException;
import com.andrey999r.staynova.exceptions.custom.registration.RegistrationFailedException;
import com.andrey999r.staynova.exceptions.custom.user.UserNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JwtTokenUtils jwtTokenUtils;
  private final UserServiceClient userServiceClient;
  private final TokenBlacklistService tokenBlacklistService;
  private final PasswordEncoder passwordEncoder;

  @Override
  public String registration(UserRequestDto request, String secret) {
    log.info("Registering new user: login={}", request.login());
    try {
      userServiceClient.createUser(request, secret);
      log.info("User created in user-service: login={}", request.login());
    } catch (HttpClientErrorException e) {
      log.error(
          "Failed to create user in user-service: login={}, status={}",
          request.login(),
          e.getStatusCode());
      throw new RegistrationFailedException(e.getMessage());
    }
    return authorization(request);
  }

  @Override
  public String authorization(UserRequestDto userRequestDto) {
    log.info("Authenticating user: login={}", userRequestDto.login());

    UserDetailsDto userDetailsDto;
    try {
      userDetailsDto = userServiceClient.getDetails(userRequestDto.login());
    } catch (HttpClientErrorException.NotFound e) {
      log.warn("User not found in user-service: login={}", userRequestDto.login());
      throw new UserNotFoundException(userRequestDto.login());
    }

    if (!passwordEncoder.matches(userRequestDto.password(), userDetailsDto.password())) {
      log.warn("Invalid password for user: login={}", userRequestDto.login());
      throw new InvalidCredentialsException();
    }

    List<GrantedAuthority> authorities =
        userDetailsDto.roles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    UserDetails userDetails =
        new User(userDetailsDto.login(), userDetailsDto.password(), authorities);

    String token = jwtTokenUtils.generateToken(userDetails);
    log.info("Token issued for user: login={}", userRequestDto.login());
    return token;
  }

  @Override
  public void logout(String authHeader) {
    log.info("Processing logout request");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      log.warn("Logout failed — invalid Authorization header");
      throw new InvalidTokenHeaderException();
    }
    String token = authHeader.substring(7);
    long expirationMls = jwtTokenUtils.getExpiration(token);
    tokenBlacklistService.blacklistToken(token, expirationMls);
    log.info("Token blacklisted successfully");
  }
}
