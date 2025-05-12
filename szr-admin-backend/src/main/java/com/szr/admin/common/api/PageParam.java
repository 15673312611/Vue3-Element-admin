package com.szr.admin.common.api;

import lombok.Data;

/**
 * 分页请求参数
 */
@Data
public class PageParam {
    /**
     * 当前页码
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer size = 10;
    
    /**
     * 排序字段
     */
    private String sortBy;
    
    /**
     * 排序方向
     */
    private String sortOrder;
} 