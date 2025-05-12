package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.PowerConfig;
import com.szr.admin.service.PowerConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 算力配置控制器
 */
@RestController
@RequestMapping("/system/power-config")
public class PowerConfigController {

    @Autowired
    private PowerConfigService powerConfigService;

    /**
     * 分页查询算力配置
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('system:power:list')")
    public Result<Page<PowerConfig>> list(
            @RequestParam(value = "operationType", required = false) Integer operationType,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<PowerConfig> result = powerConfigService.listPowerConfigs(operationType, status, page, size);
        return Result.success(result);
    }

    /**
     * 获取算力配置详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:power:query')")
    public Result<PowerConfig> getDetail(@PathVariable Long id) {
        PowerConfig powerConfig = powerConfigService.getPowerConfigDetail(id);
        return Result.success(powerConfig);
    }

    /**
     * 创建算力配置
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('system:power:add')")
    public Result<String> add(@RequestBody PowerConfig powerConfig) {
        boolean success = powerConfigService.createPowerConfig(powerConfig);
        return success ? Result.success() : Result.failed("添加失败，该操作类型的配置已存在");
    }

    /**
     * 更新算力配置
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:power:edit')")
    public Result<String> update(@PathVariable Long id, @RequestBody PowerConfig powerConfig) {
        powerConfig.setId(id);
        boolean success = powerConfigService.updatePowerConfig(powerConfig);
        return success ? Result.success() : Result.failed("更新失败");
    }

    /**
     * 删除算力配置
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:power:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = powerConfigService.deletePowerConfig(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 修改算力配置状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('system:power:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = powerConfigService.updateStatus(id, status);
        return success ? Result.success() : Result.failed("状态修改失败");
    }
}