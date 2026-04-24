package com.bio.drqi.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.bio.drqi.es"})
public class BioDrqiEsMain {

    public static void main(String[] args) {
        SpringApplication.run(BioDrqiEsMain.class, args);
    }
}
