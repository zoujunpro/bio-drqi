package com.bio.drqi.applet.wx;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import cn.binarywang.wx.miniapp.message.WxMaMessageRouter;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@EnableConfigurationProperties(WxMiniAppProperties.class)
public class WxMaConfiguration {

    public WxMiniAppProperties wxMiniAppProperties;

    public static final Map<String, WxMaMessageRouter> routers = new HashMap<>();

    public static Map<String, WxMaService> wxMaServiceMap = new HashMap<>();


    @Autowired
    public WxMaConfiguration(WxMiniAppProperties wxMiniAppProperties) {
        this.wxMiniAppProperties = wxMiniAppProperties;
    }

    public static WxMaService getMaService(String appId) {
        WxMaService wxMaService = wxMaServiceMap.get(appId);
        if (wxMaService != null) {
            return wxMaService;
        } else {
            throw new BusinessException("未找到对应的appId" + appId + "配置");
        }
    }

    public static WxMaMessageRouter getWxMaMessageRouter(String appId) {
        return routers.get(appId);
    }

    @PostConstruct
    public void init() {
        List<WxMiniAppProperties.Config> configList = wxMiniAppProperties.getConfigs();
        if (CollectionUtil.isEmpty(configList)) {
            throw new BusinessException("配置文件错误，小程序基本信息未配置或配置错误");
        }
        for (WxMiniAppProperties.Config config : configList) {
            WxMaDefaultConfigImpl defaultConfig = new WxMaDefaultConfigImpl();
            defaultConfig.setAppid(config.getAppId());
            defaultConfig.setSecret(config.getSecret());
            defaultConfig.setToken(config.getToken());
            defaultConfig.setAesKey(config.getAesKey());
            defaultConfig.setMsgDataFormat(config.getMsgDataFormat());
            WxMaService wxMaService = new WxMaServiceImpl();
            wxMaService.setWxMaConfig(defaultConfig);
            wxMaServiceMap.put(config.getAppId(), wxMaService);
            routers.put(config.getAppId(), new WxMaMessageRouter(wxMaService));
        }
    }

}
