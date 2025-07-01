package com.bio.drqi.bsm.kd;

import com.bio.drqi.bsm.kd.properties.KdProperties;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KdProperties.class)
public class KdConfiguration {


    private final KdProperties kdProperties;


    @Autowired
    public KdConfiguration(KdProperties kdProperties) {
        this.kdProperties = kdProperties;
    }


    @Bean
    public KdRequestUtil getKdRequestUtil(KdProperties kdProperties) {
        return new KdRequestUtil(kdProperties);

    }


}
