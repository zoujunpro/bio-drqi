package com.bio.drqi.ai.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AiCommandHttpConfig {

    @Bean
    @LoadBalanced
    public RestTemplate aiCommandRestTemplate() {
        return new RestTemplate();
    }
}
