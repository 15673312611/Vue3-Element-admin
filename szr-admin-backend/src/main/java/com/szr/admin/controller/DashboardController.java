package com.szr.admin.controller;

import com.szr.admin.common.api.CommonResult;
import com.szr.admin.entity.Task;
import com.szr.admin.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private TaskService taskService;

    @GetMapping("/statistics")
    public CommonResult<Map<String, Object>> getStatistics() {
        Map<String, Object> data = new HashMap<>();
        
        // 获取任务总数作为系统负载
        long taskCount = taskService.count();
        data.put("systemLoad", taskCount);
        
        // 获取最近10条任务
        List<Task> recentTasks = taskService.getRecentTasks(10);
        data.put("recentTasks", recentTasks);
        
        return CommonResult.success(data);
    }
} 