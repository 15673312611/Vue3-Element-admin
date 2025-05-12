package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.VoiceCategory;

import java.util.List;

/**
 * 音色分类Service接口
 */
public interface VoiceCategoryService extends IService<VoiceCategory> {

    /**
     * 分页查询音色分类列表
     *
     * @param keyword 关键词
     * @param status  状态
     * @param page    页码
     * @param size    每页大小
     * @return 分页数据
     */
    Page<VoiceCategory> listCategories(String keyword, Integer status, Integer page, Integer size);

    /**
     * 获取所有可用分类
     *
     * @return 分类列表
     */
    List<VoiceCategory> listAllEnabledCategories();

    /**
     * 创建音色分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean createCategory(VoiceCategory category);

    /**
     * 更新音色分类
     *
     * @param category 分类信息
     * @return 是否成功
     */
    boolean updateCategory(VoiceCategory category);

    /**
     * 删除音色分类
     *
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);

    /**
     * 更新音色分类状态
     *
     * @param id     分类ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);
} 