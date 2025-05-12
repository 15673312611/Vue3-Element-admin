package com.szr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.AdminUsers;
import com.szr.admin.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员密码重置控制器
 * 注意：此控制器仅用于开发环境，生产环境请删除
 */
@RestController
@RequestMapping("/dev")
public class AdminPasswordResetController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 重置管理员密码为admin
     */
//    @GetMapping("/reset-admin-password")
//    public Result<String> resetAdminPassword() {
//        try {
//            // 获取admin用户
//            AdminUsers admin = adminUserService.getAdminByUsername("admin");
//            if (admin == null) {
//                return Result.failed("未找到admin用户");
//            }
//
//            // 更新密码为"admin"
//            admin.setPassword(passwordEncoder.encode("admin"));
//            boolean updated = adminUserService.updateAdminUser(admin);
//
//            if (updated) {
//                return Result.success("管理员密码已重置为: admin");
//            } else {
//                return Result.failed("密码重置失败");
//            }
//        } catch (Exception e) {
//            return Result.failed("发生错误: " + e.getMessage());
//        }
//    }
} 