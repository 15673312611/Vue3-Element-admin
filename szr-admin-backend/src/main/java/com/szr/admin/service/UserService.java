package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.User;

/**
 * 前台用户Service接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 修改用户算力
     */
    boolean updatePower(Long id, Integer power);
    
    /**
     * 重置用户密码
     */
    boolean resetPassword(Long id);

    /**
     * 新增前台用户
     */
    boolean addUser(User user);
} 