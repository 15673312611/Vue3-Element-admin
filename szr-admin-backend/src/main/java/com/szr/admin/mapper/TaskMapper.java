package com.szr.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szr.admin.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务数据访问层
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
} 