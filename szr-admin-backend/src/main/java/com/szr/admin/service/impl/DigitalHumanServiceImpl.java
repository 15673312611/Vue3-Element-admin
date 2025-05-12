package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.DigitalHuman;
import com.szr.admin.entity.User;
import com.szr.admin.mapper.DigitalHumanMapper;
import com.szr.admin.mapper.UserMapper;
import com.szr.admin.service.DigitalHumanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数字人Service实现类
 */
@Service
public class DigitalHumanServiceImpl extends ServiceImpl<DigitalHumanMapper, DigitalHuman> implements DigitalHumanService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<DigitalHuman> listDigitalHumans(Long userId, String keyword, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<DigitalHuman> queryWrapper = new LambdaQueryWrapper<>();
        
        // 用户ID查询
        if (userId != null) {
            queryWrapper.eq(DigitalHuman::getUid, userId);
        }
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(DigitalHuman::getName, keyword)
                    .or()
                    .like(DigitalHuman::getDescription, keyword);
        }
        
        // 状态查询
        if (status != null) {
            queryWrapper.eq(DigitalHuman::getStatus, status.toString());
        }
        
        // 排序规则：创建时间降序
        queryWrapper.orderByDesc(DigitalHuman::getCreateTime);
        
        Page<DigitalHuman> digitalHumanPage = page(new Page<>(page, size), queryWrapper);
        
        // 获取用户信息并填充到数字人对象中
        setUsernameForDigitalHumans(digitalHumanPage.getRecords());
        
        return digitalHumanPage;
    }

    @Override
    public DigitalHuman getDigitalHumanDetail(Long id) {
        DigitalHuman digitalHuman = getById(id);
        if (digitalHuman != null && digitalHuman.getUid() != null) {
            User user = userMapper.selectById(digitalHuman.getUid());
            if (user != null) {
                digitalHuman.setUsername(user.getUsername());
            }
        }
        return digitalHuman;
    }

    /**
     * 为数字人列表设置用户名
     */
    private void setUsernameForDigitalHumans(List<DigitalHuman> digitalHumans) {
        if (digitalHumans == null || digitalHumans.isEmpty()) {
            return;
        }
        
        // 获取所有用户ID
        List<Long> userIds = digitalHumans.stream()
                .map(DigitalHuman::getUid)
                .filter(uid -> uid != null)
                .distinct()
                .collect(Collectors.toList());
        
        if (!userIds.isEmpty()) {
            // 查询用户信息
            LambdaQueryWrapper<User> userQuery = new LambdaQueryWrapper<>();
            userQuery.in(User::getId, userIds);
            List<User> users = userMapper.selectList(userQuery);
            
            // 构建用户ID到用户名的映射
            Map<Long, String> userIdToNameMap = users.stream()
                    .collect(Collectors.toMap(User::getId, User::getUsername));
            
            // 设置用户名
            for (DigitalHuman digitalHuman : digitalHumans) {
                if (digitalHuman.getUid() != null) {
                    digitalHuman.setUsername(userIdToNameMap.get(digitalHuman.getUid()));
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDigitalHuman(DigitalHuman digitalHuman) {
        // 设置初始值
        if (digitalHuman.getStatus() == null) {
            digitalHuman.setStatus("1"); // 默认启用
        }
        
        LocalDateTime now = LocalDateTime.now();
        digitalHuman.setCreateTime(now);
        digitalHuman.setUpdateTime(now);
        
        return save(digitalHuman);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDigitalHuman(DigitalHuman digitalHuman) {
        digitalHuman.setUpdateTime(LocalDateTime.now());
        return updateById(digitalHuman);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDigitalHuman(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        DigitalHuman digitalHuman = new DigitalHuman();
        digitalHuman.setId(id);
        digitalHuman.setStatus(status.toString());
        digitalHuman.setUpdateTime(LocalDateTime.now());
        return updateById(digitalHuman);
    }
}