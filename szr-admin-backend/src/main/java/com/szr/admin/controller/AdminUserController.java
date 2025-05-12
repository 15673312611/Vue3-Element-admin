package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.AdminUsers;
import com.szr.admin.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员用户控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * 分页查询管理员用户
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('system:admin:list')")
    public Result<Page<AdminUsers>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<AdminUsers> result = adminUserService.listAdminUsers(keyword, status, page, size);
        return Result.success(result);
    }
    
    /**
     * 获取管理员用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:admin:query')")
    public Result<AdminUsers> getInfo(@PathVariable Long id) {
        AdminUsers adminUsers = adminUserService.getById(id);
        return Result.success(adminUsers);
    }
    
    /**
     * 添加管理员用户
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('system:admin:add')")
    public Result<String> add(@RequestBody AdminUsers adminUsers) {
        boolean success = adminUserService.createAdminUser(adminUsers);
        return success ? Result.success() : Result.failed("添加失败");
    }
    
    /**
     * 更新管理员用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:admin:edit')")
    public Result<String> update(@PathVariable Long id, @RequestBody AdminUsers adminUsers) {
        adminUsers.setId(id);
        boolean success = adminUserService.updateAdminUser(adminUsers);
        return success ? Result.success() : Result.failed("更新失败");
    }
    
    /**
     * 删除管理员用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:admin:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = adminUserService.deleteAdminUser(id);
        return success ? Result.success() : Result.failed("删除失败");
    }
    
    /**
     * 修改管理员用户状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('system:admin:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = adminUserService.updateStatus(id, status);
        return success ? Result.success() : Result.failed("状态修改失败");
    }
    
    /**
     * 重置管理员用户密码
     */
    @PutMapping("/{id}/password/reset")
    @PreAuthorize("@securityExpression.hasPermission('system:admin:edit')")
    public Result<String> resetPassword(@PathVariable Long id) {
        boolean success = adminUserService.resetPassword(id);
        return success ? Result.success() : Result.failed("密码重置失败");
    }
} 