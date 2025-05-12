package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.PowerConfig;

/**
 * 算力配置Service接口
 */
public interface PowerConfigService extends IService<PowerConfig> {

    /**
     * 分页查询算力配置
     *
     * @param operationType 操作类型
     * @param status        状态
     * @param page          页码
     * @param size          每页大小
     * @return 分页数据
     */
    Page<PowerConfig> listPowerConfigs(Integer operationType, Integer status, Integer page, Integer size);

    /**
     * 获取算力配置详情
     *
     * @param id 配置ID
     * @return 算力配置详情
     */
    PowerConfig getPowerConfigDetail(Long id);

    /**
     * 创建算力配置
     *
     * @param powerConfig 算力配置信息
     * @return 是否成功
     */
    boolean createPowerConfig(PowerConfig powerConfig);

    /**
     * 更新算力配置
     *
     * @param powerConfig 算力配置信息
     * @return 是否成功
     */
    boolean updatePowerConfig(PowerConfig powerConfig);

    /**
     * 删除算力配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean deletePowerConfig(Long id);

    /**
     * 更新算力配置状态
     *
     * @param id     配置ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
} 