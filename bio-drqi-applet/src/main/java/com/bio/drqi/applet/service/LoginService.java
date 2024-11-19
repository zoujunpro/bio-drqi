package com.bio.drqi.applet.service;

import com.bio.base.base.LoginRspDTO;
import com.bio.drqi.applet.dto.req.WxLoginReqDTO;
import org.springframework.web.bind.annotation.RequestBody;

public interface LoginService {
    LoginRspDTO login(WxLoginReqDTO wxLoginReqDTO);
}
