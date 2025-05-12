package com.szr.admin.security.service;

import com.szr.admin.entity.AdminUsers;
import com.szr.admin.security.AdminUserDetails;
import com.szr.admin.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserDetailsService实现类
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    private final AdminUserService adminUserService;
    
    @Autowired
    public UserDetailsServiceImpl(@Lazy AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 获取管理员用户
        AdminUsers adminUsers = adminUserService.getAdminByUsername(username);
        if (adminUsers == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        
        // 获取用户权限
        List<String> permissions = adminUserService.getUserPermissions(username);
        
        // 创建UserDetails对象
        return new AdminUserDetails(adminUsers, permissions);
    }
} 