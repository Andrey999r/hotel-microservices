package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.vo.role.RoleName;

public record FindAllUsersByRoleNameQuery(Filter filter, RoleName roleName) {}
