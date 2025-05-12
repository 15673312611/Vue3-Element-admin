package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.Template;
import com.szr.admin.mapper.TemplateMapper;
import com.szr.admin.service.TemplateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 形象Service实现类
 */
@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, Template> implements TemplateService {

    @Override
    public Page<Template> listTemplates(String keyword, Long categoryId, Integer status, Integer isHot, Integer page, Integer size) {
        LambdaQueryWrapper<Template> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Template::getName, keyword)
                    .or()
                    .like(Template::getDescription, keyword);
        }
        
        // 分类查询
        if (categoryId != null) {
            queryWrapper.eq(Template::getCategoryId, categoryId);
        }
        
        // 状态查询
        if (status != null) {
            queryWrapper.eq(Template::getStatus, status.toString());
        }
        
        // 热门查询
        if (isHot != null && isHot == 1) {
            // 根据您的数据库结构，如果没有isHot字段，可以适当调整此条件
            // 例如可以通过sort字段或其他方式判断热门
            queryWrapper.orderByAsc(Template::getSort);
        }
        
        // 排序规则：排序号升序，创建时间降序
        queryWrapper.orderByAsc(Template::getSort)
                .orderByDesc(Template::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public Template getTemplateDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createTemplate(Template template) {
        // 设置初始值
        if (template.getStatus() == null) {
            template.setStatus("1"); // 默认启用
        }
        if (template.getSort() == null) {
            template.setSort(0); // 默认排序
        }
        
        LocalDateTime now = LocalDateTime.now();
        template.setCreateTime(now);
        template.setUpdateTime(now);
        
        return save(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTemplate(Template template) {
        template.setUpdateTime(LocalDateTime.now());
        return updateById(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTemplate(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteTemplates(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        Template template = new Template();
        template.setId(id);
        template.setStatus(status.toString());
        template.setUpdateTime(LocalDateTime.now());
        return updateById(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHotStatus(Long id, Integer isHot) {
        // 因为实体中没有isHot字段，根据具体情况可能需要调整为其他字段的更新
        // 例如可以将热门项的sort设为较小值，使其排在前面
        Template template = new Template();
        template.setId(id);
        if (isHot == 1) {
            // 热门项排序值设为较小
            template.setSort(0);
        } else {
            // 非热门项排序值设为较大
            template.setSort(999);
        }
        template.setUpdateTime(LocalDateTime.now());
        return updateById(template);
    }
} 