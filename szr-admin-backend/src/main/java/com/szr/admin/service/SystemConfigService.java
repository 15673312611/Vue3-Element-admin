package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置Service接口
 */
public interface SystemConfigService extends IService<SystemConfig> {

    /**
     * 分页查询系统配置
     *
     * @param configKey 配置键
     * @param groupName 分组名称
     * @param page      页码
     * @param size      每页大小
     * @return 分页数据
     */
    Page<SystemConfig> listSystemConfigs(String configKey, String groupName, Integer page, Integer size);

    /**
     * 根据分组获取配置列表
     *
     * @param groupName 分组名称
     * @return 配置列表
     */
    List<SystemConfig> getConfigsByGroup(String groupName);

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    String getConfigValue(String configKey);

    /**
     * 获取多个配置值
     *
     * @param configKeys 配置键列表
     * @return 配置键值对
     */
    Map<String, String> getConfigValues(List<String> configKeys);

    /**
     * 创建系统配置
     *
     * @param systemConfig 系统配置信息
     * @return 是否成功
     */
    boolean createSystemConfig(SystemConfig systemConfig);

    /**
     * 更新系统配置
     *
     * @param systemConfig 系统配置信息
     * @return 是否成功
     */
    boolean updateSystemConfig(SystemConfig systemConfig);

    /**
     * 批量更新系统配置
     *
     * @param configs 系统配置列表
     * @return 是否成功
     */
    boolean batchUpdateSystemConfig(List<SystemConfig> configs);

    /**
     * 删除系统配置
     *
     * @param id 配置ID
     * @return 是否成功
     */
    boolean deleteSystemConfig(Long id);
} 