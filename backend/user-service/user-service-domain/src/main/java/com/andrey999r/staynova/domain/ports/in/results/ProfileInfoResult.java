package com.andrey999r.staynova.domain.ports.in.results;

import com.andrey999r.staynova.domain.vo.user.Email;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.PicturePath;

public record ProfileInfoResult(
    Login login, Email email, boolean emailNotificationsEnabled, PicturePath picturePath) {}
