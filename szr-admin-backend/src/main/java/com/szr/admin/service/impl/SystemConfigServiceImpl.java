package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.SystemConfig;
import com.szr.admin.mapper.SystemConfigMapper;
import com.szr.admin.service.SystemConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置Service实现类
 */
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public Page<SystemConfig> listSystemConfigs(String configKey, String groupName, Integer page, Integer size) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据配置键查询
        if (StringUtils.hasText(configKey)) {
            queryWrapper.like(SystemConfig::getConfigKey, configKey);
        }
        

        
        // 按创建时间排序
        queryWrapper.orderByDesc(SystemConfig::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public List<SystemConfig> getConfigsByGroup(String groupName) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .orderByAsc(SystemConfig::getConfigKey);
        return list(queryWrapper);
    }

    @Override
    public String getConfigValue(String configKey) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = getOne(queryWrapper);
        return config != null ? config.getConfigValue() : null;
    }

    @Override
    public Map<String, String> getConfigValues(List<String> configKeys) {
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SystemConfig::getConfigKey, configKeys);
        List<SystemConfig> configs = list(queryWrapper);
        
        return configs.stream()
                .collect(Collectors.toMap(SystemConfig::getConfigKey, SystemConfig::getConfigValue, (v1, v2) -> v1));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createSystemConfig(SystemConfig systemConfig) {
        // 检查配置键是否已存在
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, systemConfig.getConfigKey());
        if (count(queryWrapper) > 0) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        systemConfig.setCreateTime(now);
        systemConfig.setUpdateTime(now);
        
        return save(systemConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateSystemConfig(SystemConfig systemConfig) {
        systemConfig.setUpdateTime(LocalDateTime.now());
        return updateById(systemConfig);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchUpdateSystemConfig(List<SystemConfig> configs) {
        if (configs == null || configs.isEmpty()) {
            return true;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 分离有ID和没有ID的配置项
        List<SystemConfig> updateConfigs = configs.stream()
                .filter(config -> config.getId() != null)
                .peek(config -> config.setUpdateTime(now))
                .collect(Collectors.toList());
        
        List<SystemConfig> createConfigs = configs.stream()
                .filter(config -> config.getId() == null)
                .peek(config -> {
                    config.setCreateTime(now);
                    config.setUpdateTime(now);
                })
                .collect(Collectors.toList());
        
        // 批量更新有ID的配置项
        boolean updateResult = true;
        if (!updateConfigs.isEmpty()) {
            updateResult = updateBatchById(updateConfigs);
        }
        
        // 批量创建没有ID的配置项
        boolean createResult = true;
        if (!createConfigs.isEmpty()) {
            // 检查配置键是否已存在
            List<String> configKeys = createConfigs.stream()
                    .map(SystemConfig::getConfigKey)
                    .collect(Collectors.toList());
            
            LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(SystemConfig::getConfigKey, configKeys);
            List<SystemConfig> existingConfigs = list(queryWrapper);
            
            // 过滤掉已存在的配置键
            List<String> existingKeys = existingConfigs.stream()
                    .map(SystemConfig::getConfigKey)
                    .collect(Collectors.toList());
            
            List<SystemConfig> newConfigs = createConfigs.stream()
                    .filter(config -> !existingKeys.contains(config.getConfigKey()))
                    .collect(Collectors.toList());
            
            if (!newConfigs.isEmpty()) {
                createResult = saveBatch(newConfigs);
            }
            
            // 更新已存在的配置项
            Map<String, SystemConfig> existingConfigMap = existingConfigs.stream()
                    .collect(Collectors.toMap(SystemConfig::getConfigKey, config -> config));
            
            List<SystemConfig> updateExistingConfigs = createConfigs.stream()
                    .filter(config -> existingKeys.contains(config.getConfigKey()))
                    .map(config -> {
                        SystemConfig existingConfig = existingConfigMap.get(config.getConfigKey());
                        existingConfig.setConfigValue(config.getConfigValue());
                        existingConfig.setUpdateTime(now);
                        return existingConfig;
                    })
                    .collect(Collectors.toList());
            
            if (!updateExistingConfigs.isEmpty()) {
                updateResult = updateResult && updateBatchById(updateExistingConfigs);
            }
        }
        
        return updateResult && createResult;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSystemConfig(Long id) {
        return removeById(id);
    }
} 