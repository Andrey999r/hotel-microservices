package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.vo.user.Login;

public interface FindUserByLoginInputPort extends QueryUseCase<Login, User> {}
