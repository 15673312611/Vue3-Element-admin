package com.szr.admin.test;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.szr.admin.entity.AdminUsers;
import com.szr.admin.mapper.AdminUserMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import com.szr.admin.SzrAdminApplication;

/**
 * 重置管理员密码工具类
 * 
 * 注意：此类仅用于开发环境临时重置密码，不应在生产环境使用
 */
public class ResetAdminPassword {
    
    public static void main(String[] args) {
        // 创建BCryptPasswordEncoder实例
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 这个是BCrypt加密的"admin"密码
        String encodedPassword = encoder.encode("admin");
        
        System.out.println("生成的加密密码: " + encodedPassword);
        System.out.println("请使用以下SQL语句更新管理员密码:");
        System.out.println("UPDATE admin_users SET password = '" + encodedPassword + "' WHERE username = 'admin';");
        
        System.out.println("\n管理员默认账号: admin");
        System.out.println("管理员默认密码: admin");
    }
} 