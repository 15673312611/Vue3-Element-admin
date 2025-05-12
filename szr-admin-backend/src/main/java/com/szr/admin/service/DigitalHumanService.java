package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.DigitalHuman;

/**
 * 数字人Service接口
 */
public interface DigitalHumanService extends IService<DigitalHuman> {

    /**
     * 分页查询数字人列表
     *
     * @param userId  用户ID
     * @param keyword 关键词
     * @param status  状态
     * @param page    页码
     * @param size    每页大小
     * @return 分页数据
     */
    Page<DigitalHuman> listDigitalHumans(Long userId, String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取数字人详情
     *
     * @param id 数字人ID
     * @return 数字人详情
     */
    DigitalHuman getDigitalHumanDetail(Long id);

    /**
     * 创建数字人
     *
     * @param digitalHuman 数字人信息
     * @return 是否成功
     */
    boolean createDigitalHuman(DigitalHuman digitalHuman);

    /**
     * 更新数字人
     *
     * @param digitalHuman 数字人信息
     * @return 是否成功
     */
    boolean updateDigitalHuman(DigitalHuman digitalHuman);

    /**
     * 删除数字人
     *
     * @param id 数字人ID
     * @return 是否成功
     */
    boolean deleteDigitalHuman(Long id);

    /**
     * 更新数字人状态
     *
     * @param id     数字人ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
} 