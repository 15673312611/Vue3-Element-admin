package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.Task;
import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService extends IService<Task> {
    
    /**
     * 分页查询任务列表
     *
     * @param page 分页参数
     * @param name 任务名称
     * @param status 任务状态
     * @return 分页任务列表
     */
    Page<Task> getTaskList(Page<Task> page, String name, String status);
    
    /**
     * 创建任务
     *
     * @param task 任务信息
     * @return 创建结果
     */
    boolean createTask(Task task);
    
    /**
     * 取消任务
     *
     * @param id 任务ID
     * @return 取消结果
     */
    boolean cancelTask(Long id);
    
    /**
     * 获取任务结果
     *
     * @param id 任务ID
     * @return 任务信息
     */
    Task getTaskResult(Long id);
    
    /**
     * 获取最近的任务列表
     *
     * @param limit 限制数量
     * @return 任务列表
     */
    List<Task> getRecentTasks(int limit);
} 