package com.bio.drqi.manage.controller;

import com.bio.base.user.rsp.UserDetailRspDTO;
import com.bio.drqi.manage.auth.rsp.LoginRspDTO;
import com.bio.common.core.dto.BusinessStatus;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.service.ClientLoginService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * CER客户端登录
 */
@RestController
public class ClientLoginController {

    @Resource
    private ClientLoginService clientLoginService;

    /**
     * 根据凭证登录客户端
     * @param ticket
     * @return
     */
    @GetMapping("/login")
    @WebLog(desc = "根据凭证登录客户端")
    public ResponseResult<LoginRspDTO> login(@RequestParam @Validated @NotBlank(message = "参数缺失：ticket") String ticket) {
        LoginRspDTO loginRspDTO = clientLoginService.login(ticket);
        if (loginRspDTO == null) {
            return ResponseResult.getError(BusinessStatus.TOKEN_LOSE.getCode(), "凭证失效");
        } else {
            return ResponseResult.getSuccess(loginRspDTO);
        }

    }

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/data")
    @WebLog(desc = "获取用户信息")
    public ResponseResult<UserDetailRspDTO> data() {
        return ResponseResult.getSuccess(clientLoginService.data());
    }

    /**
     * 客户端推出
     * @return
     */
    @GetMapping("/logout")
    @WebLog(desc = "客户端退出")
    public ResponseResult<String> logout() {
        clientLoginService.logout();
        return ResponseResult.getSuccess("退出成功");
    }

    /**
     * 客户端推出（sso回调）
     * @return
     */
    @GetMapping("/logoutCall")
    @WebLog(desc = "客户端退出回调")
    public ResponseResult<String> logoutCall(@RequestParam String appId) {
        clientLoginService.logoutCall(appId);
        return ResponseResult.getSuccess("退出成功");
    }
}
