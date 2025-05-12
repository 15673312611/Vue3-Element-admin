package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.User;
import com.szr.admin.mapper.UserMapper;
import com.szr.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

/**
 * 前台用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePower(Long id, Integer power) {
        User user = getById(id);
        if (user == null) {
            return false;
        }
        
        user.setPower(power);
        return updateById(user);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(Long id) {
        User user = getById(id);
        if (user == null) {
            return false;
        }
        
        // 密码加密
        user.setPassword(passwordEncoder.encode("123456"));
        return updateById(user);
    }

    @Override
    public boolean addUser(User user) {
        // 设置默认状态为正常
        user.setStatus("1");
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        return save(user);
    }
} 