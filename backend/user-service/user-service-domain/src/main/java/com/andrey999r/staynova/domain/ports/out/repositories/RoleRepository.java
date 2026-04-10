package com.andrey999r.staynova.domain.ports.out.repositories;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.vo.role.RoleName;

public interface RoleRepository {
  Role findByRoleName(RoleName roleName);

  Filtered<Role> findAll(Filter filter);

  boolean existsByRoleName(RoleName roleName);

  void save(Role role);
}
