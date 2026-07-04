package com.bio.drqi.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.mybatis.spring.annotation.MapperScan;

/**
 * AI独立服务启动类。
 * 该服务负责自然语言理解、命令选择和远程调用现有业务系统接口，不直接承载业务实现。
 */
@SpringBootApplication(scanBasePackages = {"com.bio"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = {"com.bio"})
@EnableScheduling
@MapperScan(basePackages = "com.bio.drqi.ai.mapper")
public class BioAIAdmin {

    public static void main(String[] args) {
        SpringApplication.run(BioAIAdmin.class, args);
    }
}
