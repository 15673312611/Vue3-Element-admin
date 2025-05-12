package com.szr.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.DigitalHuman;
import com.szr.admin.service.DigitalHumanService;
import com.szr.admin.util.AliyunOSSUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 数字人控制器
 */
@Slf4j
@RestController
@RequestMapping("/digital-human")
public class DigitalHumanController {

    @Autowired
    private DigitalHumanService digitalHumanService;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 分页查询数字人列表
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('digital:human:list')")
    public Result<Page<DigitalHuman>> list(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<DigitalHuman> result = digitalHumanService.listDigitalHumans(userId, keyword, status, page, size);
        return Result.success(result);
    }

    /**
     * 获取数字人详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('digital:human:query')")
    public Result<DigitalHuman> getDetail(@PathVariable Long id) {
        DigitalHuman digitalHuman = digitalHumanService.getDigitalHumanDetail(id);
        return Result.success(digitalHuman);
    }

    /**
     * 创建数字人
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('digital:human:add')")
    public Result<String> add(@RequestParam(value = "file", required = false) MultipartFile file, 
                             @RequestParam("digitalHuman") String digitalHumanJson) {
        try {
            // 解析数字人数据
            DigitalHuman digitalHuman = objectMapper.readValue(digitalHumanJson, DigitalHuman.class);
            
            // 上传视频文件
            if (file != null && !file.isEmpty()) {
                String videoUrl = AliyunOSSUtil.uploadFile(file);
                // 移除URL中的查询参数
                String baseUrl = videoUrl.split("\\?")[0];
                digitalHuman.setVideoUrl(baseUrl);
                // 生成封面URL
                digitalHuman.setCoverUrl(baseUrl + "?x-oss-process=video/snapshot,t_0");
            }
            
            boolean success = digitalHumanService.createDigitalHuman(digitalHuman);
            return success ? Result.success() : Result.failed("添加失败");
        } catch (Exception e) {
            log.error("新增数字人失败", e);
            return Result.failed("新增数字人失败：" + e.getMessage());
        }
    }

    /**
     * 更新数字人
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('digital:human:edit')")
    public Result<String> update(@PathVariable Long id, 
                                @RequestParam(value = "file", required = false) MultipartFile file, 
                                @RequestParam("digitalHuman") String digitalHumanJson) {
        try {
            // 解析数字人数据
            DigitalHuman digitalHuman = objectMapper.readValue(digitalHumanJson, DigitalHuman.class);
            digitalHuman.setId(id);
            
            // 如果上传了新视频，则处理视频文件
            if (file != null && !file.isEmpty()) {
                String videoUrl = AliyunOSSUtil.uploadFile(file);
                // 移除URL中的查询参数
                String baseUrl = videoUrl.split("\\?")[0];
                digitalHuman.setVideoUrl(baseUrl);
                // 生成封面URL
                digitalHuman.setCoverUrl(baseUrl + "?x-oss-process=video/snapshot,t_0");
            }
            
            boolean success = digitalHumanService.updateDigitalHuman(digitalHuman);
            return success ? Result.success() : Result.failed("更新失败");
        } catch (Exception e) {
            log.error("修改数字人失败", e);
            return Result.failed("修改数字人失败：" + e.getMessage());
        }
    }

    /**
     * 删除数字人
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('digital:human:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = digitalHumanService.deleteDigitalHuman(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 修改数字人状态
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("@securityExpression.hasPermission('digital:human:edit')")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        boolean success = digitalHumanService.updateStatus(id, status);
        return success ? Result.success() : Result.failed("状态修改失败");
    }
} 