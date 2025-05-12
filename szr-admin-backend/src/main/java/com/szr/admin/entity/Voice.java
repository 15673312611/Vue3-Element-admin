package com.szr.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 音色实体类
 */
@Data
@TableName("voice_table")
public class Voice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 音色名称
     */
    private String name;

    /**
     * 性别：1-男，2-女
     */
    private Integer gender;

    /**
     * 音色状态
     */
    private String status;

    /**
     * 对应原始音色ID
     */
    private Integer voiceId;

    /**
     * 声音文件链接URL
     */
    private String voiceUrl;

    /**
     * 音色分类
     */
    private Integer categoryId;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;

    /**
     * 是否热门：0-否，1-是
     */
    private Integer isHot;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
} 