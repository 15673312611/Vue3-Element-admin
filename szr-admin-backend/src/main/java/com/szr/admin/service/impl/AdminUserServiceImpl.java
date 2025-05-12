package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.AdminUsers;
import com.szr.admin.mapper.AdminUserMapper;
import com.szr.admin.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 管理员用户Service实现类
 */
@Service
@RefreshScope
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUsers> implements AdminUserService {
    
    private final PasswordEncoder passwordEncoder;
    
    @Value("${system.password-salt}")
    private String passwordSalt;
    
    @Autowired
    public AdminUserServiceImpl(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public AdminUsers getAdminByUsername(String username) {
        return getOne(new LambdaQueryWrapper<AdminUsers>().eq(AdminUsers::getUsername, username));
    }
    
    @Override
    public List<String> getUserPermissions(String username) {
        try {
            // 超级管理员拥有所有权限
            if ("admin".equals(username)) {
                // 查询所有可用权限
                List<String> allPermissions = baseMapper.getAllPermissions();
                // 确保添加了所有模块的权限
                if (!allPermissions.contains("*:*:*")) {
                    allPermissions.add("*:*:*");  // 添加通配符权限
                }
                return allPermissions;
            }
            
            return baseMapper.getUserPermissions(username);
        } catch (Exception e) {
            // 出错时返回空列表
            return new ArrayList<>();
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAdminUser(AdminUsers adminUsers) {
        // 密码加密
        adminUsers.setPassword(passwordEncoder.encode(adminUsers.getPassword()));
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        adminUsers.setCreateTime(now);
        adminUsers.setUpdateTime(now);
        
        // 设置默认状态为正常
        if (adminUsers.getStatus() == null) {
            adminUsers.setStatus(1);
        }
        
        // 保存用户
        return save(adminUsers);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminUser(AdminUsers adminUsers) {
        // 设置更新时间
        adminUsers.setUpdateTime(LocalDateTime.now());
        
        // 更新用户
        return updateById(adminUsers);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteAdminUser(Long id) {
        // 删除用户
        return removeById(id);
    }
    
    @Override
    public Page<AdminUsers> listAdminUsers(String keyword, Integer status, Integer page, Integer size) {
        // 构建查询条件
        LambdaQueryWrapper<AdminUsers> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据关键字模糊查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(AdminUsers::getUsername, keyword)
                    .or().like(AdminUsers::getNickname, keyword)
                    .or().like(AdminUsers::getEmail, keyword);
        }
        
        // 根据状态查询
        if (status != null) {
            queryWrapper.eq(AdminUsers::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(AdminUsers::getCreateTime);
        
        // 分页查询
        return page(new Page<>(page, size), queryWrapper);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 更新状态
        AdminUsers adminUsers = new AdminUsers();
        adminUsers.setId(id);
        adminUsers.setStatus(status);
        adminUsers.setUpdateTime(LocalDateTime.now());
        
        return updateById(adminUsers);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id) {
        // 更新密码
        AdminUsers adminUsers = new AdminUsers();
        adminUsers.setId(id);
        adminUsers.setPassword(passwordEncoder.encode("123456")); // 默认密码为123456
        adminUsers.setUpdateTime(LocalDateTime.now());
        
        return updateById(adminUsers);
    }
} 