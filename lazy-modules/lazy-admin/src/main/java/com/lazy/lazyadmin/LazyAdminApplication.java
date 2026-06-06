package com.lazy.lazyadmin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.lazy.lazyadmin.mapper")
@SpringBootApplication
public class LazyAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(LazyAdminApplication.class, args);
    }

}
