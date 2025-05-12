package com.szr.admin.common.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页数据封装类
 */
@Data
public class CommonPage<T> {
    private Long pageNum;
    private Long pageSize;
    private Long totalPage;
    private Long total;
    private List<T> records;

    /**
     * 将MyBatis Plus分页结果转化为通用结果
     */
    public static <T> CommonPage<T> restPage(Page<T> pageResult) {
        CommonPage<T> result = new CommonPage<T>();
        result.setPageNum(pageResult.getCurrent());
        result.setPageSize(pageResult.getSize());
        result.setTotal(pageResult.getTotal());
        result.setTotalPage(pageResult.getPages());
        result.setRecords(pageResult.getRecords());
        return result;
    }
} 