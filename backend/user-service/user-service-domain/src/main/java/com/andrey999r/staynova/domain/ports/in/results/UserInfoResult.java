package com.andrey999r.staynova.domain.ports.in.results;

import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.Password;
import java.util.List;

public record UserInfoResult(Login login, Password password, List<RoleName> roleNames) {}
