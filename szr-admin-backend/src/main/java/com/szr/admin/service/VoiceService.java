package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.Voice;

import java.util.List;

/**
 * 音色Service接口
 */
public interface VoiceService extends IService<Voice> {

    /**
     * 分页查询音色列表
     *
     * @param keyword    关键词
     * @param categoryId 分类ID
     * @param status     状态
     * @param isHot      是否热门
     * @param page       页码
     * @param size       每页大小
     * @return 分页数据
     */
    Page<Voice> listVoices(String keyword, Long categoryId, String status, Integer isHot, Integer page, Integer size);

    /**
     * 获取音色详情
     *
     * @param id 音色ID
     * @return 音色详情
     */
    Voice getVoiceDetail(Long id);

    /**
     * 创建音色
     *
     * @param voice 音色信息
     * @return 是否成功
     */
    boolean createVoice(Voice voice);

    /**
     * 更新音色
     *
     * @param voice 音色信息
     * @return 是否成功
     */
    boolean updateVoice(Voice voice);

    /**
     * 删除音色
     *
     * @param id 音色ID
     * @return 是否成功
     */
    boolean deleteVoice(Long id);

    /**
     * 批量删除音色
     *
     * @param ids 音色ID列表
     * @return 是否成功
     */
    boolean batchDeleteVoices(List<Long> ids);

    /**
     * 更新音色状态
     *
     * @param id     音色ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Integer id, String status);

    /**
     * 更新音色热门状态
     *
     * @param id    音色ID
     * @param isHot 是否热门
     * @return 是否成功
     */
    boolean updateHotStatus(Integer id, Integer isHot);
} 