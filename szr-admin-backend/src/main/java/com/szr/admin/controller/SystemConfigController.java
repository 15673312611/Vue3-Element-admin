package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.SystemConfig;
import com.szr.admin.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/system/config")
@Slf4j
public class SystemConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 分页查询系统配置
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('system:config:list')")
    public Result<Page<SystemConfig>> list(
            @RequestParam(value = "configKey", required = false) String configKey,
            @RequestParam(value = "groupName", required = false) String groupName,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<SystemConfig> result = systemConfigService.listSystemConfigs(configKey, groupName, page, size);
        return Result.success(result);
    }

    /**
     * 根据分组获取配置列表
     */
    @GetMapping("/group/{groupName}")
    @PreAuthorize("@securityExpression.hasPermission('system:config:list')")
    public Result<List<SystemConfig>> getConfigsByGroup(@PathVariable String groupName) {
        List<SystemConfig> configs = systemConfigService.getConfigsByGroup(groupName);
        return Result.success(configs);
    }

    /**
     * 根据配置键获取配置值
     */
    @GetMapping("/key/{configKey}")
    @PreAuthorize("@securityExpression.hasPermission('system:config:query')")
    public Result<String> getConfigValue(@PathVariable String configKey) {
        String value = systemConfigService.getConfigValue(configKey);
        return Result.success(value);
    }

    /**
     * 获取多个配置值
     */
    @PostMapping("/values")
    @PreAuthorize("@securityExpression.hasPermission('system:config:query')")
    public Result<Map<String, String>> getConfigValues(@RequestBody List<String> configKeys) {
        Map<String, String> values = systemConfigService.getConfigValues(configKeys);
        return Result.success(values);
    }

    /**
     * 创建系统配置
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('system:config:add')")
    public Result<String> add(@RequestBody SystemConfig systemConfig) {
        boolean success = systemConfigService.createSystemConfig(systemConfig);
        return success ? Result.success() : Result.failed("添加失败，配置键已存在");
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:config:edit')")
    public Result<String> update(@PathVariable Long id, @RequestBody SystemConfig systemConfig) {
        systemConfig.setId(id);
        boolean success = systemConfigService.updateSystemConfig(systemConfig);
        return success ? Result.success() : Result.failed("更新失败");
    }

    /**
     * 批量更新系统配置
     */
    @PutMapping("/batch")
    @PreAuthorize("@securityExpression.hasPermission('system:config:edit')")
    public Result<String> batchUpdate(@RequestBody List<SystemConfig> configs) {
        try {
            boolean success = systemConfigService.batchUpdateSystemConfig(configs);
            return success ? Result.success() : Result.failed("批量更新失败");
        } catch (Exception e) {
            log.error("批量更新系统配置失败", e);
            return Result.failed("批量更新失败: " + e.getMessage());
        }
    }

    /**
     * 批量创建或更新系统配置
     */
    @PostMapping("/batch")
    @PreAuthorize("@securityExpression.hasPermission('system:config:edit')")
    public Result<String> batchCreateOrUpdate(@RequestBody List<SystemConfig> configs) {
        try {
            boolean success = systemConfigService.batchUpdateSystemConfig(configs);
            return success ? Result.success() : Result.failed("批量创建或更新失败");
        } catch (Exception e) {
            log.error("批量创建或更新系统配置失败", e);
            return Result.failed("批量创建或更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除系统配置
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:config:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = systemConfigService.deleteSystemConfig(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 获取公共配置（不需要权限，用于前端初始化获取系统名称和Logo等）
     */
    @GetMapping("/public")
    public Result<Map<String, String>> getPublicConfig() {
        try {
            List<String> configKeys = Arrays.asList("system_name", "system_logo");
            Map<String, String> configs = systemConfigService.getConfigValues(configKeys);
            return Result.success(configs);
        } catch (Exception e) {
            log.error("获取公共配置失败", e);
            return Result.failed("获取公共配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有系统设置（分组返回）
     */
    @GetMapping("/settings")
    @PreAuthorize("@securityExpression.hasPermission('system:config:query')")
    public Result<Map<String, Object>> getSettings() {
        try {
            // 获取所有配置
            List<SystemConfig> allConfigs = systemConfigService.list();
            
            // 根据配置键前缀分组
            Map<String, Object> result = new HashMap<>();
            Map<String, String> basic = new HashMap<>();
            Map<String, String> security = new HashMap<>();
            Map<String, String> storage = new HashMap<>();
            
            for (SystemConfig config : allConfigs) {
                String key = config.getConfigKey();
                String value = config.getConfigValue();
                
                if (key.startsWith("system_")) {
                    basic.put(key, value);
                } else if (key.startsWith("security_")) {
                    security.put(key, value);
                } else if (key.startsWith("storage_")) {
                    storage.put(key, value);
                }
            }
            
            result.put("basic", basic);
            result.put("security", security);
            result.put("storage", storage);
            
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取系统设置失败", e);
            return Result.failed("获取系统设置失败: " + e.getMessage());
        }
    }

    /**
     * 测试公共配置接口
     */
    @GetMapping("/test-public")
    public Result<Map<String, String>> testPublicConfig() {
        Map<String, String> configs = new HashMap<>();
        configs.put("system_name", "测试系统名称");
        configs.put("system_logo", "/static/test-logo.svg");
        return Result.success(configs);
    }
} 