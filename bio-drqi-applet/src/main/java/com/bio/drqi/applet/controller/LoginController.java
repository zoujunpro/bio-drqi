package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.WxLoginReqDTO;
import org.springframework.web.bind.annotation.RequestBody;

public class LoginController {

    public ResponseResult<String> login(@RequestBody WxLoginReqDTO wxLoginReqDTO){
        return null;
    }
}
