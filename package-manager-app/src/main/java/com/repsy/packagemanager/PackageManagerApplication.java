package com.repsy.packagemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.repsy.packagemanager", "com.repsy.storage"})
public class PackageManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PackageManagerApplication.class, args);
    }
}
