package com.szr.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szr.admin.entity.Template;

import java.util.List;

/**
 * 形象Service接口
 */
public interface TemplateService extends IService<Template> {

    /**
     * 分页查询形象列表
     *
     * @param keyword    关键词
     * @param categoryId 分类ID
     * @param status     状态
     * @param isHot      是否热门
     * @param page       页码
     * @param size       每页大小
     * @return 分页数据
     */
    Page<Template> listTemplates(String keyword, Long categoryId, Integer status, Integer isHot, Integer page, Integer size);

    /**
     * 获取形象详情
     *
     * @param id 形象ID
     * @return 形象详情
     */
    Template getTemplateDetail(Long id);

    /**
     * 创建形象
     *
     * @param template 形象信息
     * @return 是否成功
     */
    boolean createTemplate(Template template);

    /**
     * 更新形象
     *
     * @param template 形象信息
     * @return 是否成功
     */
    boolean updateTemplate(Template template);

    /**
     * 删除形象
     *
     * @param id 形象ID
     * @return 是否成功
     */
    boolean deleteTemplate(Long id);

    /**
     * 批量删除形象
     *
     * @param ids 形象ID列表
     * @return 是否成功
     */
    boolean batchDeleteTemplates(List<Long> ids);

    /**
     * 更新形象状态
     *
     * @param id     形象ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 更新形象热门状态
     *
     * @param id    形象ID
     * @param isHot 是否热门
     * @return 是否成功
     */
    boolean updateHotStatus(Long id, Integer isHot);
} 