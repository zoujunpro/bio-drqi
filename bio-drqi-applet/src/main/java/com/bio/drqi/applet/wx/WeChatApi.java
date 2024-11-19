package com.bio.drqi.applet.wx;

import com.bio.drqi.applet.wx.dto.JsCode2sessionRspDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wechatFeign", url = "https://api.weixin.qq.com/")
public interface WeChatApi {

    @GetMapping("/sns/jscode2session")
    JsCode2sessionRspDTO jsCode2session(@RequestParam("appid") String appid, @RequestParam("secret") String secret, @RequestParam("js_code") String js_code, @RequestParam("grant_type") String grant_type);

}
