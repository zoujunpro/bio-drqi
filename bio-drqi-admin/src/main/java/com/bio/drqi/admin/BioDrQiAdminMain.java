package com.bio.drqi.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.bio"})
@EnableDiscoveryClient
@MapperScan(basePackages = {"com.bio.drqi.mapper", "com.bio.drqi.document.mapper"})
@EnableFeignClients(basePackages = {"com.bio.print.api","com.bio.base.api","com.bio.drqi.external.client","com.bio.drqi.manage.feign"})
@EnableScheduling
public class BioDrQiAdminMain {
    public static void main(String[] args) {
        SpringApplication.run(BioDrQiAdminMain.class,args);
    }
}
