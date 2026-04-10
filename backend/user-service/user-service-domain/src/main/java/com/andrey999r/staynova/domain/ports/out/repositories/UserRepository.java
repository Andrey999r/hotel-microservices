package com.andrey999r.staynova.domain.ports.out.repositories;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;

public interface UserRepository {
  User findByLogin(Login login);

  boolean existsByLogin(Login login);

  Filtered<User> findAllByRoleName(Filter filter, RoleName roleName);

  void save(User user);
}
