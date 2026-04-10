package com.andrey999r.staynova.domain.ports.in.commands;

import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;

public record AddRoleToUserCommand(Login login, RoleName roleName) {}
