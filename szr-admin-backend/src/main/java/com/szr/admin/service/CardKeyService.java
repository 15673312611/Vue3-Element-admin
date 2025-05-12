package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.CardKey;

import java.util.List;

/**
 * 卡密Service接口
 */
public interface CardKeyService extends IService<CardKey> {

    /**
     * 分页查询卡密列表
     *
     * @param cardNo  卡密号
     * @param type    卡密类型
     * @param status  状态
     * @param batchNo 批次号
     * @param page    页码
     * @param size    每页大小
     * @return 分页数据
     */
    Page<CardKey> listCardKeys(String cardNo, Integer type, String status, String batchNo, Integer page, Integer size);

    /**
     * 获取卡密详情
     *
     * @param id 卡密ID
     * @return 卡密详情
     */
    CardKey getCardKeyDetail(Long id);

    /**
     * 创建卡密
     *
     * @param cardKey 卡密信息
     * @return 是否成功
     */
    boolean createCardKey(CardKey cardKey);

    /**
     * 批量生成卡密
     *
     * @param type      卡密类型
     * @param value     面值
     * @param count     生成数量
     * @param validDays 有效期天数
     * @param batchNo   批次号
     * @param remark    备注
     * @return 生成的卡密列表
     */
    List<CardKey> batchGenerateCardKeys(Integer type, Integer value, Integer count, Integer validDays, String batchNo, String remark);

    /**
     * 更新卡密
     *
     * @param cardKey 卡密信息
     * @return 是否成功
     */
    boolean updateCardKey(CardKey cardKey);

    /**
     * 删除卡密
     *
     * @param id 卡密ID
     * @return 是否成功
     */
    boolean deleteCardKey(Long id);

    /**
     * 批量删除卡密
     *
     * @param ids 卡密ID列表
     * @return 是否成功
     */
    boolean batchDeleteCardKeys(List<Long> ids);

    /**
     * 导出卡密
     *
     * @param batchNo 批次号
     * @return 导出的卡密列表
     */
    List<CardKey> exportCardKeys(String batchNo);
} 