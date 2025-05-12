package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.PowerConfig;
import com.szr.admin.mapper.PowerConfigMapper;
import com.szr.admin.service.PowerConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 算力配置Service实现类
 */
@Service
public class PowerConfigServiceImpl extends ServiceImpl<PowerConfigMapper, PowerConfig> implements PowerConfigService {

    @Override
    public Page<PowerConfig> listPowerConfigs(Integer operationType, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<PowerConfig> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据操作类型查询
        if (operationType != null) {
            queryWrapper.eq(PowerConfig::getOperationType, operationType);
        }
        
        // 根据状态查询
        if (status != null) {
            queryWrapper.eq(PowerConfig::getStatus, status);
        }
        
        // 排序规则
        queryWrapper.orderByAsc(PowerConfig::getOperationType)
                .orderByDesc(PowerConfig::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public PowerConfig getPowerConfigDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPowerConfig(PowerConfig powerConfig) {
        // 检查同一操作类型的配置是否已存在
        LambdaQueryWrapper<PowerConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PowerConfig::getOperationType, powerConfig.getOperationType());
        if (count(queryWrapper) > 0) {
            return false;
        }
        
        // 设置初始值
        if (powerConfig.getStatus() == null) {
            powerConfig.setStatus(1); // 默认启用
        }
        
        LocalDateTime now = LocalDateTime.now();
        powerConfig.setCreateTime(now);
        powerConfig.setUpdateTime(now);
        
        return save(powerConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePowerConfig(PowerConfig powerConfig) {
        powerConfig.setUpdateTime(LocalDateTime.now());
        return updateById(powerConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePowerConfig(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        PowerConfig powerConfig = new PowerConfig();
        powerConfig.setId(id);
        powerConfig.setStatus(status);
        powerConfig.setUpdateTime(LocalDateTime.now());
        return updateById(powerConfig);
    }
} 