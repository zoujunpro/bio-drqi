package com.bio.drqi.applet;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.bio.drqi","com.bio.common","com.bio.print","com.bio.base"})
@EnableDiscoveryClient
@MapperScan(basePackages = "com.bio.drqi.mapper")
@EnableFeignClients(basePackages = {"com.bio.print.api","com.bio.base.api"})
@EnableScheduling
public class BioDrqiAppletMain {
    public static void main(String[] args) {
        SpringApplication.run(BioDrqiAppletMain.class,args);
    }

}
