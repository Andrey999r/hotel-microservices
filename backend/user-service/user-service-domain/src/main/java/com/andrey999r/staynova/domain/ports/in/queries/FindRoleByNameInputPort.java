package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.vo.role.RoleName;

public interface FindRoleByNameInputPort extends QueryUseCase<RoleName, Role> {}
