package com.szr.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.entity.CardKey;
import org.apache.ibatis.annotations.Param;

/**
 * 卡密Mapper接口
 */
public interface CardKeyMapper extends BaseMapper<CardKey> {

    /**
     * 分页查询卡密列表，带用户信息
     *
     * @param page    分页参数
     * @param cardNo  卡密号
     * @param type    卡密类型
     * @param status  状态
     * @param batchNo 批次号
     * @return 分页数据
     */
    IPage<CardKey> selectCardKeyPage(Page<CardKey> page,
                                    @Param("cardNo") String cardNo,
                                    @Param("type") Integer type,
                                    @Param("status") Integer status,
                                    @Param("batchNo") String batchNo);

    /**
     * 根据ID查询卡密详情，带用户信息
     *
     * @param id 卡密ID
     * @return 卡密详情
     */
    CardKey selectCardKeyById(Long id);
} 