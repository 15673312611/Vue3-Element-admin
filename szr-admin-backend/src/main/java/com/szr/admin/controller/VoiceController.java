package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.Voice;
import com.szr.admin.service.VoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音色控制器
 */
@RestController
@RequestMapping("/voice")
public class VoiceController {

    @Autowired
    private VoiceService voiceService;

    /**
     * 分页查询音色列表
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('voice:list')")
    public Result<Page<Voice>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "isHot", required = false) Integer isHot,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<Voice> result = voiceService.listVoices(keyword, categoryId, status, isHot, page, size);
        return Result.success(result);
    }

    /**
     * 获取音色详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('voice:query')")
    public Result<Voice> getDetail(@PathVariable Long id) {
        Voice voice = voiceService.getVoiceDetail(id);
        return Result.success(voice);
    }

    /**
     * 创建音色
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('voice:add')")
    public Result<String> add(@RequestBody Voice voice) {
        boolean success = voiceService.createVoice(voice);
        return success ? Result.success() : Result.failed("添加失败");
    }

    /**
     * 更新音色
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('voice:edit')")
    public Result<String> update(@PathVariable Integer id, @RequestBody Voice voice) {
        voice.setId(id);
        boolean success = voiceService.updateVoice(voice);
        return success ? Result.success() : Result.failed("更新失败");
    }

    /**
     * 删除音色
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('voice:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = voiceService.deleteVoice(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 批量删除音色
     */
    @DeleteMapping("/batch")
    @PreAuthorize("@securityExpression.hasPermission('voice:remove')")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        boolean success = voiceService.batchDeleteVoices(ids);
        return success ? Result.success() : Result.failed("批量删除失败");
    }

    /**
     * 修改音色状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('voice:edit')")
    public Result<String> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        boolean success = voiceService.updateStatus(id, status);
        return success ? Result.success() : Result.failed("状态修改失败");
    }

    /**
     * 修改音色热门状态
     */
    @PutMapping("/{id}/hot")
    @PreAuthorize("@securityExpression.hasPermission('voice:edit')")
    public Result<String> updateHotStatus(@PathVariable Integer id, @RequestParam Integer isHot) {
        boolean success = voiceService.updateHotStatus(id, isHot);
        return success ? Result.success() : Result.failed("热门状态修改失败");
    }
} 