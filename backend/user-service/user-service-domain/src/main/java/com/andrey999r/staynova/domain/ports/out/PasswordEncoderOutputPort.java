package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.vo.user.Password;

public interface PasswordEncoderOutputPort {
  Password encodePassword(Password password);
}
