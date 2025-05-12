package com.szr.admin.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.Template;
import com.szr.admin.service.TemplateService;
import com.szr.admin.util.AliyunOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 形象控制器
 */
@Slf4j
@RestController
@RequestMapping("/template")
public class TemplateController {

    @Autowired
    private TemplateService templateService;



    /**
     * 分页查询形象列表
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('template:list')")
    public Result<Page<Template>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "isHot", required = false) Integer isHot,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Template> result = templateService.listTemplates(keyword, categoryId, status, isHot, page, size);
        return Result.success(result);
    }

    /**
     * 获取形象详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('template:query')")
    public Result<Template> getDetail(@PathVariable Long id) {
        Template template = templateService.getTemplateDetail(id);
        return Result.success(template);
    }

    /**
     * 创建形象
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('template:add')")
    public Result<Void> add(@RequestParam("file") MultipartFile file, @RequestParam("template") String templateJson) {
        try {
            // 解析模板数据
            Template template = JSON.parseObject(templateJson, Template.class);
            
            // 上传视频文件
            if (file != null && !file.isEmpty()) {
                String videoUrl = AliyunOSSUtil.uploadFile(file);
                // 移除URL中的查询参数
                String baseUrl = videoUrl.split("\\?")[0];
                template.setVideoUrl(baseUrl);
                // 生成封面URL
                template.setCoverUrl(baseUrl + "?x-oss-process=video/snapshot,t_0");
            }
            
            templateService.save(template);
            return Result.success();
        } catch (Exception e) {
            log.error("新增形象失败", e);
            return Result.failed("新增形象失败：" + e.getMessage());
        }
    }

    /**
     * 更新形象
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('template:edit')")
    public Result<Void> update(@PathVariable Long id, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("template") String templateJson) {
        try {
            // 解析模板数据
            Template template = JSON.parseObject(templateJson, Template.class);
            template.setId(id);
            
            // 如果上传了新视频，则处理视频文件
            if (file != null && !file.isEmpty()) {
                String videoUrl = AliyunOSSUtil.uploadFile(file);
                // 移除URL中的查询参数
                String baseUrl = videoUrl.split("\\?")[0];
                template.setVideoUrl(baseUrl);
                // 生成封面URL
                template.setCoverUrl(baseUrl + "?x-oss-process=video/snapshot,t_0");
            }
            
            templateService.updateById(template);
            return Result.success();
        } catch (Exception e) {
            log.error("修改形象失败", e);
            return Result.failed("修改形象失败：" + e.getMessage());
        }
    }

    /**
     * 删除形象
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('template:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = templateService.deleteTemplate(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 批量删除形象
     */
    @DeleteMapping("/batch")
    @PreAuthorize("@securityExpression.hasPermission('template:remove')")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        boolean success = templateService.batchDeleteTemplates(ids);
        return success ? Result.success() : Result.failed("批量删除失败");
    }

    /**
     * 修改形象状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('template:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = templateService.updateStatus(id, status);
        return success ? Result.success() : Result.failed("状态修改失败");
    }

    /**
     * 修改形象热门状态
     */
    @PutMapping("/{id}/hot")
    @PreAuthorize("@securityExpression.hasPermission('template:edit')")
    public Result<String> updateHotStatus(@PathVariable Long id, @RequestParam Integer isHot) {
        boolean success = templateService.updateHotStatus(id, isHot);
        return success ? Result.success() : Result.failed("热门状态修改失败");
    }
} 