package com.szr.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 形象实体类
 */
@Data
@TableName("template")
public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 形象ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 形象名称
     */
    private String name;

    /**
     * 形象描述
     */
    private String description;

    /**
     * 形象分类ID
     */
    private Long categoryId;

    /**
     * 封面图片URL
     */
    private String coverUrl;

    /**
     * 视频URL
     */
    private String videoUrl;

    /**
     * 性别
     */
    private String gender;

    /**
     * 排序
     */
    private Integer sort;

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