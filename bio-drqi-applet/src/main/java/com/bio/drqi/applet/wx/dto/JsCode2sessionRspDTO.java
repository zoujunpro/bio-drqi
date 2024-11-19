package com.bio.drqi.applet.wx.dto;

import lombok.Data;

@Data
public class JsCode2sessionRspDTO {

    private String openid;
    private String session_key;
    private String unionid;
    private String errcode;
    private String errmsg;
}
