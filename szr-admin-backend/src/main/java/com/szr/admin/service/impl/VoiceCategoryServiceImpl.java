package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.VoiceCategory;
import com.szr.admin.mapper.VoiceCategoryMapper;
import com.szr.admin.service.VoiceCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 音色分类Service实现类
 */
@Service
public class VoiceCategoryServiceImpl extends ServiceImpl<VoiceCategoryMapper, VoiceCategory> implements VoiceCategoryService {

    @Override
    public Page<VoiceCategory> listCategories(String keyword, Integer status, Integer page, Integer size) {
        LambdaQueryWrapper<VoiceCategory> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(VoiceCategory::getName, keyword);
        }
        
        // 状态查询
        if (status != null) {
            queryWrapper.eq(VoiceCategory::getStatus, status);
        }
        
        // 排序规则：排序号升序，创建时间降序
        queryWrapper
                .orderByDesc(VoiceCategory::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public List<VoiceCategory> listAllEnabledCategories() {
        LambdaQueryWrapper<VoiceCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoiceCategory::getStatus, 1);
        return list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(VoiceCategory category) {
        // 设置初始值
        if (category.getStatus() == null) {
            category.setStatus(1); // 默认启用
        }

        
        LocalDateTime now = LocalDateTime.now();
        category.setCreateTime(now);

        return save(category);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(VoiceCategory category) {
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
        VoiceCategory category = new VoiceCategory();
        category.setId(id);
        category.setStatus(status);
        return updateById(category);
    }
} 