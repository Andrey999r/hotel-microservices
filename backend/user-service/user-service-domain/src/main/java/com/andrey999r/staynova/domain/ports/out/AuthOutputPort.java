package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import java.util.List;

public interface AuthOutputPort {
  Login getUserLogin();

  List<RoleName> getUserRoleNames();
}
