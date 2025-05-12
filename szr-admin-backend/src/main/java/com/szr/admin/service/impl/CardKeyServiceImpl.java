package com.szr.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szr.admin.entity.CardKey;
import com.szr.admin.mapper.CardKeyMapper;
import com.szr.admin.service.CardKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡密Service实现类
 */
@Service
public class CardKeyServiceImpl extends ServiceImpl<CardKeyMapper, CardKey> implements CardKeyService {

    @Override
    public Page<CardKey> listCardKeys(String cardNo, Integer type, String status, String batchNo, Integer page, Integer size) {
        LambdaQueryWrapper<CardKey> queryWrapper = new LambdaQueryWrapper<>();
        
        // 卡密号查询
        if (StringUtils.hasText(cardNo)) {
            queryWrapper.like(CardKey::getCardNo, cardNo);
        }
        
        // 状态查询
        if (status != null&&!"".equals(status)) {
            queryWrapper.eq(CardKey::getStatus, status);
        }
        
        // 排序规则：创建时间降序
        queryWrapper.orderByDesc(CardKey::getCreateTime);
        
        return page(new Page<>(page, size), queryWrapper);
    }

    @Override
    public CardKey getCardKeyDetail(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCardKey(CardKey cardKey) {
        // 设置初始值
        if (cardKey.getStatus() == null) {
            cardKey.setStatus("UNUSED"); // 默认未使用
        }
        
        // 如果没有设置卡密号，则自动生成
        if (!StringUtils.hasText(cardKey.getCardNo())) {
            cardKey.setCardNo(generateCardNo());
        }
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        cardKey.setCreateTime(now);
        cardKey.setUpdateTime(now);
        
        return save(cardKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<CardKey> batchGenerateCardKeys(Integer type, Integer value, Integer count, Integer validDays, String batchNo, String remark) {
        List<CardKey> cardKeys = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        
        // 批量生成卡密
        for (int i = 0; i < count; i++) {
            CardKey cardKey = new CardKey();
            cardKey.setCardNo(generateCardNo());
            cardKey.setAmount(value);
            cardKey.setStatus("UNUSED"); // 未使用
            cardKey.setCreateTime(now);
            cardKey.setUpdateTime(now);
            
            cardKeys.add(cardKey);
        }
        
        // 批量保存
        saveBatch(cardKeys);
        
        return cardKeys;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCardKey(CardKey cardKey) {
        cardKey.setUpdateTime(LocalDateTime.now());
        return updateById(cardKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCardKey(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteCardKeys(List<Long> ids) {
        return removeByIds(ids);
    }

    @Override
    public List<CardKey> exportCardKeys(String batchNo) {
        // 根据批次号查询卡密 这块有问题 todo
        LambdaQueryWrapper<CardKey> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CardKey::getCardNo, batchNo);
        return list(queryWrapper);
    }
    
    /**
     * 生成卡密号
     */
    private String generateCardNo() {
        return "CK" + System.currentTimeMillis() + RandomUtil.randomNumbers(6);
    }
} 