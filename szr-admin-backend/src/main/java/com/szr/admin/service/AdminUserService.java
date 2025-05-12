package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.AdminUsers;

import java.util.List;

/**
 * 管理员用户Service接口
 */
public interface AdminUserService extends IService<AdminUsers> {
    
    /**
     * 根据用户名获取管理员用户
     */
    AdminUsers getAdminByUsername(String username);
    
    /**
     * 获取用户权限列表
     */
    List<String> getUserPermissions(String username);
    
    /**
     * 创建管理员用户
     */
    boolean createAdminUser(AdminUsers adminUsers);
    
    /**
     * 更新管理员用户
     */
    boolean updateAdminUser(AdminUsers adminUsers);
    
    /**
     * 删除管理员用户
     */
    boolean deleteAdminUser(Long id);
    
    /**
     * 分页查询管理员用户
     */
    Page<AdminUsers> listAdminUsers(String keyword, Integer status, Integer page, Integer size);
    
    /**
     * 修改管理员用户状态
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 重置管理员用户密码
     */
    boolean resetPassword(Long id);
} 