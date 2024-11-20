package com.bio.drqi.applet.controller;

import com.bio.base.base.LoginRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.WxLoginReqDTO;
import com.bio.drqi.applet.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 登录
 */
@RestController
public class LoginController {

    @Resource
    private LoginService loginService;

    /**
     * 登录接口
     * @param wxLoginReqDTO
     * @return
     */
    @PostMapping("/login")
    public ResponseResult<LoginRspDTO> login(@RequestBody WxLoginReqDTO wxLoginReqDTO){
        return ResponseResult.getSuccess(loginService.login(wxLoginReqDTO));
    }
}
