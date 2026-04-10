package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.vo.role.RoleName;

public interface RoleTypeOutputPort {
  RoleName getAdminRoleName();

  RoleName getUserRoleName();
}
