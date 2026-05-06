package com.semi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SemiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SemiApplication.class, args);
    }
}
