package com.andrey999r.staynova.infrastructure.adapters.out.security;

import com.andrey999r.staynova.domain.ports.out.PasswordEncoderOutputPort;
import com.andrey999r.staynova.domain.vo.user.Password;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BCryptPasswordEncoderOutputAdapter implements PasswordEncoderOutputPort {
  private final PasswordEncoder passwordEncoder;

  @Override
  public Password encodePassword(Password password) {
    log.debug("Encoding password");
    String encodedPassword = passwordEncoder.encode(password.password());
    return new Password(encodedPassword);
  }
}
