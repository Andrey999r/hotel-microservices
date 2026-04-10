package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import java.util.List;

public interface FindAllRolesInputPort extends QueryUseCase<Filter, List<Role>> {}
