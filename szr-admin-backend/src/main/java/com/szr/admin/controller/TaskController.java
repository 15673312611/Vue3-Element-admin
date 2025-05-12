package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.CommonPage;
import com.szr.admin.common.api.CommonResult;
import com.szr.admin.entity.Task;
import com.szr.admin.security.SecurityExpressionSupport;
import com.szr.admin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务管理控制器
 */
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;
    
    @Autowired
    private SecurityExpressionSupport securityExpression;

    /**
     * 获取任务列表
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('system:task:list')")
    public CommonResult<CommonPage<Task>> list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "status", required = false) String status) {
        
        Page<Task> taskPage = taskService.getTaskList(new Page<>(page, size), name, status);
        return CommonResult.success(CommonPage.restPage(taskPage));
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:task:query')")
    public CommonResult<Task> getDetail(@PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task != null) {
            return CommonResult.success(task);
        } else {
            return CommonResult.failed("任务不存在");
        }
    }

    /**
     * 创建任务
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('system:task:add')")
    public CommonResult<String> create(@RequestBody Task task) {
        try {
            boolean success = taskService.createTask(task);
            if (success) {
                return CommonResult.success("创建成功");
            } else {
                return CommonResult.failed("创建失败");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 取消任务
     */
    @PutMapping("/{id}/cancel")
    @PreAuthorize("@securityExpression.hasPermission('system:task:edit')")
    public CommonResult<String> cancel(@PathVariable Long id) {
        try {
            boolean success = taskService.cancelTask(id);
            if (success) {
                return CommonResult.success("取消成功");
            } else {
                return CommonResult.failed("取消失败");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 获取任务结果
     */
    @GetMapping("/{id}/result")
    @PreAuthorize("@securityExpression.hasPermission('system:task:query')")
    public CommonResult<Task> getResult(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskResult(id);
            if (task != null) {
                return CommonResult.success(task);
            } else {
                return CommonResult.failed("任务不存在");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    /**
     * 删除任务
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('system:task:remove')")
    public CommonResult<String> delete(@PathVariable Long id) {
        try {
            boolean success = taskService.removeById(id);
            if (success) {
                return CommonResult.success("删除成功");
            } else {
                return CommonResult.failed("删除失败");
            }
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
} 