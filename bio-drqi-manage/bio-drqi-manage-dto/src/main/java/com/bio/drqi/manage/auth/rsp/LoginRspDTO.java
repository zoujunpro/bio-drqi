package com.bio.drqi.manage.auth.rsp;

import lombok.Data;

@Data
public class LoginRspDTO {

    /**登录凭证*/
    private String token;

    /**凭证有效期*/
    private String expires;
}
