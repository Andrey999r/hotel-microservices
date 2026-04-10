package com.andrey999r.staynova.domain.models;

import com.andrey999r.staynova.domain.vo.user.*;
import java.time.Instant;
import java.util.Set;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
  private UserId id;
  private Login login;
  private Password password;
  private Set<Role> roles;
  private PicturePath picturePath;
  private Email email;
  private boolean emailSubscriptionStatus;
  private Instant creationDate;
}
