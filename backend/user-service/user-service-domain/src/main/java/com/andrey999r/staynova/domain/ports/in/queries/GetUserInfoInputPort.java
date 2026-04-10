package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.ports.in.results.UserInfoResult;

// to do -> make this command
public interface GetUserInfoInputPort extends QueryUseCase<GetUserInfoQuery, UserInfoResult> {}
