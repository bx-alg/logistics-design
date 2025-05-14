package com.logistics.design;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

/**
 * 数智化供应链平台应用程序启动类
 */
@SpringBootApplication
@MapperScan("com.logistics.design.mapper")
@OpenAPIDefinition
public class LogisticsDesignApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsDesignApplication.class, args);
    }
} 