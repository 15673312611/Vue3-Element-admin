package com.szr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.User;
import com.szr.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 前台用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 分页查询前台用户
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('user:list')")
    public Result<Page<User>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 根据关键字模糊查询
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword);
        }
        
        // 根据状态查询
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(User::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(User::getCreateTime);
        
        // 分页查询
        Page<User> result = userService.page(new Page<>(page, size), queryWrapper);
        return Result.success(result);
    }
    
    /**
     * 获取前台用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('user:query')")
    public Result<User> getInfo(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }
    
    /**
     * 修改前台用户状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('user:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> params) {
        String status = params.get("status");
        if (status == null) {
            return Result.failed("状态参数不能为空");
        }
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        boolean success = userService.updateById(user);
        return success ? Result.success() : Result.failed("状态修改失败");
    }
    
    /**
     * 修改前台用户算力
     */
    @PutMapping("/{id}/power")
    @PreAuthorize("@securityExpression.hasPermission('user:edit')")
    public Result<String> updatePower(@PathVariable Long id, @RequestParam Integer power) {
        boolean success = userService.updatePower(id, power);
        return success ? Result.success() : Result.failed("算力修改失败");
    }
    
    /**
     * 重置前台用户密码
     */
    @PutMapping("/{id}/password/reset")
    @PreAuthorize("@securityExpression.hasPermission('user:edit')")
    public Result<String> resetPassword(@PathVariable Long id) {
        boolean success = userService.resetPassword(id);
        return success ? Result.success() : Result.failed("密码重置失败");
    }
    
    /**
     * 新增前台用户
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('user:add')")
    public Result<String> add(@RequestBody User user) {
        // 生成六位随机邀请码，确保唯一性
        String inviteCode;
        do {
            inviteCode = generateRandomInviteCode();
        } while (userService.lambdaQuery().eq(User::getInviteCode, inviteCode).count() > 0);
        user.setInviteCode(inviteCode);
        boolean success = userService.addUser(user);
        return success ? Result.success() : Result.failed("新增用户失败");
    }

    /**
     * 生成六位随机邀请码
     */
    private String generateRandomInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }

    /**
     * 编辑前台用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('user:edit')")
    public Result<String> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        boolean success = userService.updateById(user);
        return success ? Result.success() : Result.failed("编辑用户失败");
    }
} 