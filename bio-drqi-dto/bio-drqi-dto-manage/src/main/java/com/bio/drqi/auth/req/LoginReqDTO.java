package com.bio.drqi.auth.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginReqDTO {

    @NotBlank(message = "用户名缺失")
    private String username;
    @NotBlank(message = "密码缺失")
    private String password;

    /**系统标识*/
    private String sysCode;
}
