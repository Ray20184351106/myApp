package com.mes.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MesSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(MesSystemApplication.class, args);
        System.out.println("MES System Service Started Successfully!");
    }
}
