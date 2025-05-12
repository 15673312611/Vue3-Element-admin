package com.szr.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.util.Date;

/**
 * 任务实体类
 */
@Data
@TableName("task")
public class Task {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务名称
     */
    private String name;
    
    /**
     * 任务内容
     */
    private String text;
    
    /**
     * 视频URL
     */
    private String videoUrl;
    
    /**
     * 任务状态：1 待处理，2 处理中，3 已完成，4  失败
     */
    private String status;
    
    /**
     * 音色ID
     */
    private Long voiceId;
    
    /**
     * 音频URL
     */
    private String audioUrl;
    
    /**
     * 结果URL
     */
    private String resultUrl;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 音频ID列表
     */
    private String audioIds;
    
    /**
     * API ID
     */
    private String apiId;
    
    /**
     * API日志
     */
    private String apiLog;
} 