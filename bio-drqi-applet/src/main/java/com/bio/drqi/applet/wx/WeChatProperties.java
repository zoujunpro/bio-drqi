package com.bio.drqi.applet.wx;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bio.properties")
@Data
public class WeChatProperties {

    private String appId="wxfdf5c2ae277168c0";

    private String secret="45ab8bf5e22569636c88de319c14c775";
}
