package com.bio.drqi.applet.controller;

import com.bio.base.base.LoginRspDTO;
import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.applet.dto.req.WxLoginReqDTO;
import com.bio.drqi.applet.service.LoginService;
import org.springframework.web.bind.annotation.*;

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


    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/data")
    @WebLog(desc = "获取用户信息")
    public ResponseResult<UserDetailRspDTO> data() {
        return ResponseResult.getSuccess(loginService.data());
    }

    /**
     * 客户端推出
     * @return
     */
    @GetMapping("/logout")
    @WebLog(desc = "客户端退出")
    public ResponseResult<String> logout(String appId) {
        loginService.logout(appId);
        return ResponseResult.getSuccess("退出成功");
    }

    /**
     * 客户端推出（sso回调）
     * @return
     */
    @GetMapping("/logoutCall")
    @WebLog(desc = "客户端退出回调")
    public ResponseResult<String> logoutCall(@RequestParam String appId) {
        loginService.logoutCall(appId);
        return ResponseResult.getSuccess("退出成功");
    }
}
