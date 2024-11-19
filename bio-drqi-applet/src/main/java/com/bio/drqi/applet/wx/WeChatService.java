package com.bio.drqi.applet.wx;

import com.bio.drqi.applet.wx.dto.JsCode2sessionRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WeChatService {


    @Resource
    private WeChatApi weChatApi;

    @Resource
    private WeChatProperties weChatProperties;

    public JsCode2sessionRspDTO jsCode2session(String code) {
        return weChatApi.jsCode2session(weChatProperties.getAppId(), weChatProperties.getSecret(), code, "authorization_code");
    }
}
