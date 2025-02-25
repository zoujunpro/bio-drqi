package com.bio.drqi.applet.service;

import com.bio.base.base.LoginRspDTO;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.applet.dto.req.WxLoginReqDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginService {
    LoginRspDTO login(WxLoginReqDTO wxLoginReqDTO);

    UserDetailRspDTO data();

    void logout(String appId);

    void logoutCall(String appId);
}
