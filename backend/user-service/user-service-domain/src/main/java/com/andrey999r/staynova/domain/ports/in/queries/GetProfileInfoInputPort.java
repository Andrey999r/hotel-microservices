package com.andrey999r.staynova.domain.ports.in.queries;

import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;

public interface GetProfileInfoInputPort extends QueryUseCase<Void, ProfileInfoResult> {}
