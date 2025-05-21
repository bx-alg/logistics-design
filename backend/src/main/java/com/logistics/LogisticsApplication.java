package com.logistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.mybatis.spring.annotation.MapperScan;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;

/**
 * 物流管理系统启动类
 */
@SpringBootApplication(scanBasePackages = {"com.logistics"})
@EnableTransactionManagement
@MapperScan("com.logistics.mapper")
@OpenAPIDefinition
public class LogisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogisticsApplication.class, args);
    }
} 