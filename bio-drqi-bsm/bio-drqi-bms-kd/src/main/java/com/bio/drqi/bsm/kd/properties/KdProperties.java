package com.bio.drqi.bsm.kd.properties;

import com.kingdee.bos.webapi.entity.IdentifyInfo;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cer.properties.kd")
@Data
public class KdProperties {
    private String serverUrl;
    private String acctId;
    private String appId;
    private String username;
    private String appSec;


    public  IdentifyInfo getIdentifyInfo() {
        IdentifyInfo identifyInfo = new IdentifyInfo();
        identifyInfo.setServerUrl(serverUrl);
        identifyInfo.setUserName(username);
        identifyInfo.setAppId(appId);
        identifyInfo.setAppSecret(appSec);
        identifyInfo.setdCID(acctId);
        return identifyInfo;
    }


}
