package com.bio.drqi.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.bio"})
@EnableDiscoveryClient
@MapperScan(basePackages = "com.bio.drqi.mapper")
@EnableFeignClients(basePackages = {"com.bio.print.api","com.bio.base.api","com.bio.drqi.external.client"})
@EnableScheduling
public class BioDrqiAdminMain {
    public static void main(String[] args) {
        SpringApplication.run(BioDrqiAdminMain.class,args);
    }
}
