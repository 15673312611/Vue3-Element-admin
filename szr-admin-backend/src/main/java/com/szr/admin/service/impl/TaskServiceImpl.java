package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.Task;
import com.szr.admin.entity.Voice;
import com.szr.admin.mapper.TaskMapper;
import com.szr.admin.service.TaskService;
import com.szr.admin.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 任务服务实现类
 */
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private VoiceService voiceService;

    @Override
    public Page<Task> getTaskList(Page<Task> page, String name, String status) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Task::getName, name);
        }
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq(Task::getStatus, status);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Task::getCreateTime);
        
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean createTask(Task task) {
        // 检查音色是否存在
        Voice voice = voiceService.getById(task.getVoiceId());
        if (voice == null) {
            throw new RuntimeException("音色不存在");
        }
        
        // 设置任务初始状态
        task.setStatus("pending"); // 待处理
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        
        // 保存任务
        return this.save(task);
    }

    @Override
    public boolean cancelTask(Long id) {
        Task task = this.getById(id);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 只有待处理和处理中的任务可以取消
        if ("pending".equals(task.getStatus()) || "processing".equals(task.getStatus())) {
            task.setStatus("failed"); // 设为失败状态
            task.setErrorMessage("任务已取消");
            task.setUpdateTime(new Date());
            return this.updateById(task);
        } else {
            throw new RuntimeException("当前状态的任务无法取消");
        }
    }

    @Override
    public Task getTaskResult(Long id) {
        return this.getById(id);
    }

    @Override
    public List<Task> getRecentTasks(int limit) {
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Task::getCreateTime);
        queryWrapper.last("LIMIT " + limit);
        return this.list(queryWrapper);
    }
} 