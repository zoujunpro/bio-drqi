package com.bio.drqi.es;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"com.bio"})
@EnableDiscoveryClient
@MapperScan(basePackages = "com.bio.drqi.mapper")
public class
BioDrqiEsMain {

    public static void main(String[] args) {
        SpringApplication.run(BioDrqiEsMain.class, args);
    }
}
