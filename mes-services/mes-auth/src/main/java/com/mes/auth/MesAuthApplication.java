package com.mes.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.mes"})
public class MesAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(MesAuthApplication.class, args);
        System.out.println("MES Auth Service Started Successfully!");
    }
}
