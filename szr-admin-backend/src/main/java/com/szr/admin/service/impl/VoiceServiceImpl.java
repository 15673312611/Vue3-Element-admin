package com.szr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.Voice;
import com.szr.admin.entity.VoiceCategory;
import com.szr.admin.mapper.VoiceMapper;
import com.szr.admin.mapper.VoiceCategoryMapper;
import com.szr.admin.service.VoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 音色Service实现类
 */
@Service
public class VoiceServiceImpl extends ServiceImpl<VoiceMapper, Voice> implements VoiceService {

    @Autowired
    private VoiceCategoryMapper voiceCategoryMapper;

    @Override
    public Page<Voice> listVoices(String keyword, Long categoryId, String status, Integer isHot, Integer page, Integer size) {
        LambdaQueryWrapper<Voice> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(Voice::getName, keyword)
                    .or()
                    .like(Voice::getGender, keyword);
        }
        
        // 分类查询
        if (categoryId != null) {
            queryWrapper.eq(Voice::getCategoryId, categoryId);
        }
        
        // 状态查询
        if (status != null&&!"".equals(status)) {
            queryWrapper.eq(Voice::getStatus, status);
        }
        
        // 热门查询
        if (isHot != null) {
            queryWrapper.eq(Voice::getIsHot, isHot);
        }
        
        // 排序规则：创建时间降序
        queryWrapper.orderByDesc(Voice::getCreateTime);
        
        Page<Voice> voicePage = page(new Page<>(page, size), queryWrapper);
        
        // 获取所有分类ID
        List<Integer> categoryIds = voicePage.getRecords().stream()
                .map(Voice::getCategoryId)
                .filter(id -> id != null)
                .collect(Collectors.toList());
        
        if (!categoryIds.isEmpty()) {
            // 查询分类信息
            List<VoiceCategory> categories = voiceCategoryMapper.selectBatchIds(categoryIds);
            Map<Integer, String> categoryMap = categories.stream()
                    .collect(Collectors.toMap(
                        category -> category.getId().intValue(),
                        VoiceCategory::getName
                    ));
            
            // 设置分类名称
            voicePage.getRecords().forEach(voice -> {
                if (voice.getCategoryId() != null) {
                    voice.setCategoryName(categoryMap.get(voice.getCategoryId()));
                }
            });
        }
        
        return voicePage;
    }

    @Override
    public Voice getVoiceDetail(Long id) {
        Voice voice = getById(id);
        if (voice != null && voice.getCategoryId() != null) {
            VoiceCategory category = voiceCategoryMapper.selectById(voice.getCategoryId());
            if (category != null) {
                voice.setCategoryName(category.getName());
            }
        }
        return voice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createVoice(Voice voice) {
        // 设置初始值
        if (voice.getStatus() == null) {
            voice.setStatus("completed"); // 默认正常
        }
        if (voice.getIsHot() == null) {
            voice.setIsHot(0); // 默认非热门
        }

        LocalDateTime now = LocalDateTime.now();
        voice.setCreateTime(now);

        return save(voice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateVoice(Voice voice) {
        return updateById(voice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteVoice(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteVoices(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Integer id, String status) {
        Voice voice = new Voice();
        voice.setId(id);
        voice.setStatus(status);
        return updateById(voice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateHotStatus(Integer id, Integer isHot) {
        Voice voice = new Voice();
        voice.setId(id);
        voice.setIsHot(isHot);
        return updateById(voice);
    }
} 