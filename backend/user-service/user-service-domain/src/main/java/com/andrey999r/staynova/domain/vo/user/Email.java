package com.andrey999r.staynova.domain.vo.user;

import com.andrey999r.staynova.domain.exceptions.user.BlankEmailException;
import com.andrey999r.staynova.domain.exceptions.user.InvalidFormatEmailException;
import com.andrey999r.staynova.domain.exceptions.user.NullEmailException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record Email(String email) {

  private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
      Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

  public Email {
    validateEmail(email);
  }

  private void validateEmail(String email) {
    ensureNotNull(email);
    ensureNotBlank(email);
    ensureHasValidFormat(email);
  }

  private void ensureNotNull(String email) {
    if (email == null) {
      throw new NullEmailException();
    }
  }

  private void ensureNotBlank(String email) {
    if (email.isBlank()) {
      throw new BlankEmailException();
    }
  }

  private void ensureHasValidFormat(String email) {
    if (!hasValidFormat(email)) {
      throw new InvalidFormatEmailException();
    }
  }

  private boolean hasValidFormat(String email) {
    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
    return matcher.matches();
  }
}
