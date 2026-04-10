package com.andrey999r.staynova.domain.ports.in.commands;

import com.andrey999r.staynova.domain.ports.in.QueryUseCase;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;

// to do -> make this command
public interface UpdateProfileInputPort
    extends QueryUseCase<UpdateProfileCommand, ProfileInfoResult> {}
