package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.VoiceCategory;
import com.szr.admin.service.VoiceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音色分类控制器
 */
@RestController
@RequestMapping("/voice/category")
public class VoiceCategoryController {

    @Autowired
    private VoiceCategoryService categoryService;

    /**
     * 分页查询音色分类
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('voice:category:list')")
    public Result<Page<VoiceCategory>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<VoiceCategory> result = categoryService.listCategories(keyword, status, page, size);
        return Result.success(result);
    }

    /**
     * 获取所有启用的分类列表
     */
    @GetMapping("/listAll")
    @PreAuthorize("@securityExpression.hasPermission('voice:category:list')")
    public Result<List<VoiceCategory>> listAllEnabled() {
        List<VoiceCategory> list = categoryService.listAllEnabledCategories();
        return Result.success(list);
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('voice:category:query')")
    public Result<VoiceCategory> getDetail(@PathVariable Long id) {
        VoiceCategory category = categoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('voice:category:add')")
    public Result<String> add(@RequestBody VoiceCategory category) {
        boolean success = categoryService.createCategory(category);
        return success ? Result.success() : Result.failed("添加失败");
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('voice:category:edit')")
    public Result<String> update(@PathVariable Long id, @RequestBody VoiceCategory category) {
        category.setId(id);
        boolean success = categoryService.updateCategory(category);
        return success ? Result.success() : Result.failed("更新失败");
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('voice:category:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = categoryService.deleteCategory(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 修改分类状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('voice:category:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = categoryService.updateStatus(id, status);
        return success ? Result.success() : Result.failed("状态修改失败");
    }
} 