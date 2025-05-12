package com.szr.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 卡密实体类
 */
@Data
@TableName("card_key")
public class CardKey implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 卡密号
     */
    private String cardNo;

    /**
     * 充值金额
     */
    private Integer amount;

    /**
     * 状态：UNUSED未使用，USED已使用
     */
    private String status;

    /**
     * 使用者ID
     */
    private Long usedBy;

    /**
     * 使用时间
     */
    private LocalDateTime usedAt;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 用户信息，非数据库字段
     */
    @TableField(exist = false)
    private User user;
} 