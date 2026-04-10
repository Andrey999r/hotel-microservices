package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.vo.role.RoleName;

public interface GetAdminRoleNameInputPort extends QueryUseCase<Void, RoleName> {}
