package com.szr.admin.service.impl;

import com.szr.admin.entity.AdminUsers;
import com.szr.admin.security.AdminUserDetails;
import com.szr.admin.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户详情服务实现类
 */
@Service
@Primary
public class AdminUserDetailsServiceImpl implements UserDetailsService {
    
    private final AdminUserService adminUserService;
    
    @Autowired
    public AdminUserDetailsServiceImpl(@Lazy AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            System.out.println("正在加载用户: " + username);
            
            // 获取管理员用户信息
            AdminUsers adminUsers = adminUserService.getAdminByUsername(username);
            if (adminUsers == null) {
                System.err.println("用户不存在: " + username);
                throw new UsernameNotFoundException("用户名或密码错误");
            }
            
            System.out.println("找到用户: " + username);
            
            // 获取用户权限
            List<String> permissions = adminUserService.getUserPermissions(username);
            if (permissions == null) {
                permissions = new ArrayList<>();
            }
            
            // 返回UserDetails对象
            return new AdminUserDetails(adminUsers, permissions);
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            System.err.println("加载用户过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            throw new UsernameNotFoundException("认证过程中发生错误", e);
        }
    }
} 