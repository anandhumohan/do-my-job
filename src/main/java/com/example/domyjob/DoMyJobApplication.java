package com.example.domyjob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DoMyJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoMyJobApplication.class, args);
    }

}
