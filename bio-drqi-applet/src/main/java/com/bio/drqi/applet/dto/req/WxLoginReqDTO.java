package com.bio.drqi.applet.dto.req;

import lombok.Data;

@Data
public class WxLoginReqDTO {
    private String iv;
    private String encryptedData;
    private String appId;
    private String code;
}
