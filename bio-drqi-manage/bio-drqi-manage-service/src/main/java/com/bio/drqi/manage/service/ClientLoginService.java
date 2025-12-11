package com.bio.drqi.manage.service;


import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.manage.auth.rsp.LoginRspDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;

public interface ClientLoginService {
    LoginRspDTO login(@RequestParam @Validated @NotBlank(message = "参数缺失：ticket") String ticket);
    UserDetailRspDTO data();

    void logout();

    void logoutCall(String appId);
}
