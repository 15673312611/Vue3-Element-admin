package com.szr.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.entity.DigitalHuman;
import org.apache.ibatis.annotations.Param;

/**
 * 数字人Mapper接口
 */
public interface DigitalHumanMapper extends BaseMapper<DigitalHuman> {

    /**
     * 分页查询数字人列表，带用户、形象、音色信息
     *
     * @param page 分页参数
     * @param userId 用户ID
     * @param keyword 关键词
     * @param status 状态
     * @return 分页数据
     */
    IPage<DigitalHuman> selectDigitalHumanPage(Page<DigitalHuman> page,
                                              @Param("userId") Long userId,
                                              @Param("keyword") String keyword,
                                              @Param("status") Integer status);

    /**
     * 根据ID查询数字人详情，带用户、形象、音色信息
     *
     * @param id 数字人ID
     * @return 数字人详情
     */
    DigitalHuman selectDigitalHumanById(Long id);
} 