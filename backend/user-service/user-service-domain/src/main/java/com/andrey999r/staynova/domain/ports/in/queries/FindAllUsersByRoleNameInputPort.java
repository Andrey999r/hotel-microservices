package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import java.util.List;

public interface FindAllUsersByRoleNameInputPort
    extends QueryUseCase<FindAllUsersByRoleNameQuery, List<User>> {}
