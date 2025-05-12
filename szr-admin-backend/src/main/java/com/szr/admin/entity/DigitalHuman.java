package com.szr.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 数字人实体类
 */
@Data
@TableName("digital_human")
public class DigitalHuman implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数字人ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long uid;

    /**
     * 用户名称
     */
    @TableField(exist = false)
    private String username;

    /**
     * 数字人名称
     */
    private String name;

    /**
     * 描述信息
     */
    private String description;

    /**
     * 视频地址
     */
    private String videoUrl;

    /**
     * 封面图片地址
     */
    private String coverUrl;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
} 