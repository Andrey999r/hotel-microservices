package com.andrey999r.staynova.domain.models;

import com.andrey999r.staynova.domain.vo.role.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
  private RoleId id;
  private RoleName roleName;
}
