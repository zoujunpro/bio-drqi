package com.bio.drqi.ai.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI 独立服务启动类。
 */
@SpringBootApplication(scanBasePackages = {
        "com.bio.drqi.ai",
        "com.bio.drqi.manage.service",
        "com.bio.drqi.feishu",
        "com.bio.drqi.common",
        "com.bio.drqi.util"
})
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.bio.drqi.mapper"})
@EnableFeignClients(basePackages = {"com.bio.print.api", "com.bio.base.api", "com.bio.drqi.external.client", "com.bio.drqi.manage.feign"})
@EnableScheduling
public class BioDrQiAiAdminMain {

    public static void main(String[] args) {
        SpringApplication.run(BioDrQiAiAdminMain.class, args);
    }
}
