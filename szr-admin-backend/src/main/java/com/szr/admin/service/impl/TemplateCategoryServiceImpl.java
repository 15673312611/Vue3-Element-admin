package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.TemplateCategory;
import com.szr.admin.mapper.TemplateCategoryMapper;
import com.szr.admin.service.TemplateCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 形象分类Service实现类
 */
@Service
public class TemplateCategoryServiceImpl extends ServiceImpl<TemplateCategoryMapper, TemplateCategory> implements TemplateCategoryService {

    @Override
    public Page<TemplateCategory> listCategories(String keyword, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<TemplateCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(TemplateCategory::getName, keyword);
        }
        
        // 状态查询
        if (status != null) {
            queryWrapper.eq(TemplateCategory::getStatus, status.toString());
        }
        
        // 排序规则：排序号升序，创建时间降序
        queryWrapper.orderByAsc(TemplateCategory::getSort)
                .orderByDesc(TemplateCategory::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public List<TemplateCategory> listAllEnabledCategories() {
        LambdaQueryWrapper<TemplateCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateCategory::getStatus, "1") // 只查询启用的分类
                .orderByAsc(TemplateCategory::getSort);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(TemplateCategory category) {
        // 设置初始值
        if (category.getStatus() == null) {
            category.setStatus("1"); // 默认启用
        }
        if (category.getSort() == null) {
            category.setSort(0); // 默认排序
        }
        
        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);
        category.setUpdateTime(now);
        
        return save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(TemplateCategory category) {
        category.setUpdateTime(LocalDateTime.now());
        return updateById(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        TemplateCategory category = new TemplateCategory();
        category.setId(id);
        category.setStatus(status.toString());
        category.setUpdateTime(LocalDateTime.now());
        return updateById(category);
    }
} 