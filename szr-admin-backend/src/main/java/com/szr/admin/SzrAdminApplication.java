package com.szr.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数字人后台管理系统启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.szr.admin.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SzrAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(SzrAdminApplication.class, args);
    }
} 