package com.szr.admin.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试工具类
 */
public class PasswordTest {
    
    public static void main(String[] args) {
        // 创建BCryptPasswordEncoder实例
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 存储在数据库中的已加密密码
        String storedPassword = "$2a$10$N9qo8uLOickgx2ZMRZoMy.MrqK3X6RfE2YAG7q4hB6zVf3Q2QJQ1a";
        
        // 尝试不同的原始密码进行匹配测试
        String[] passwordsToTry = {
            "admin", "123456", "admin123", "password", "adminadmin"
        };
        
        for (String password : passwordsToTry) {
            boolean matches = encoder.matches(password, storedPassword);
            System.out.println("密码 [" + password + "] 匹配结果: " + matches);
        }
        
        // 生成一个新的加密密码（用于重置密码）
        String newPassword = "admin123";
        String encodedNewPassword = encoder.encode(newPassword);
        System.out.println("新密码 [" + newPassword + "] 加密后: " + encodedNewPassword);
        System.out.println("验证新密码: " + encoder.matches(newPassword, encodedNewPassword));
    }
} 