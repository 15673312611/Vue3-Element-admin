package com.szr.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.admin.common.api.Result;
import com.szr.admin.entity.CardKey;
import com.szr.admin.service.CardKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 卡密控制器
 */
@RestController
@RequestMapping("/card-key")
public class CardKeyController {

    @Autowired
    private CardKeyService cardKeyService;

    /**
     * 分页查询卡密列表
     */
    @GetMapping("/list")
    @PreAuthorize("@securityExpression.hasPermission('card:key:list')")
    public Result<Page<CardKey>> list(
            @RequestParam(value = "cardNo", required = false) String cardNo,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<CardKey> result = cardKeyService.listCardKeys(cardNo, null, status, null, page, size);
        return Result.success(result);
    }

    /**
     * 获取卡密详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('card:key:query')")
    public Result<CardKey> getDetail(@PathVariable Long id) {
        CardKey cardKey = cardKeyService.getCardKeyDetail(id);
        return Result.success(cardKey);
    }

    /**
     * 创建卡密
     */
    @PostMapping
    @PreAuthorize("@securityExpression.hasPermission('card:key:add')")
    public Result<String> add(@RequestBody CardKey cardKey) {
        boolean success = cardKeyService.createCardKey(cardKey);
        return success ? Result.success() : Result.failed("添加失败");
    }

    /**
     * 批量生成卡密
     */
    @PostMapping("/batch")
    @PreAuthorize("@securityExpression.hasPermission('card:key:add')")
    public Result<Map<String, Object>> batchGenerate(
            @RequestParam("type") Integer type,
            @RequestParam("value") Integer value,
            @RequestParam("count") Integer count) {
        
        String batchNo = "B" + System.currentTimeMillis();
        
        List<CardKey> cardKeys = cardKeyService.batchGenerateCardKeys(type, value, count, 0, batchNo, null);
        
        Map<String, Object> data = new HashMap<>();
        data.put("count", cardKeys.size());
        data.put("batchNo", batchNo);
        
        return Result.success(data);
    }

    /**
     * 更新卡密
     */
    @PutMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('card:key:edit')")
    public Result<String> update(@PathVariable Long id, @RequestBody CardKey cardKey) {
        cardKey.setId(id);
        boolean success = cardKeyService.updateCardKey(cardKey);
        return success ? Result.success() : Result.failed("更新失败");
    }

    /**
     * 删除卡密
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@securityExpression.hasPermission('card:key:remove')")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = cardKeyService.deleteCardKey(id);
        return success ? Result.success() : Result.failed("删除失败");
    }

    /**
     * 批量删除卡密
     */
    @DeleteMapping("/batch")
    @PreAuthorize("@securityExpression.hasPermission('card:key:remove')")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        boolean success = cardKeyService.batchDeleteCardKeys(ids);
        return success ? Result.success() : Result.failed("批量删除失败");
    }

    /**
     * 导出卡密
     */
    @GetMapping("/export")
    @PreAuthorize("@securityExpression.hasPermission('card:key:export')")
    public Result<List<CardKey>> export(@RequestParam("batchNo") String batchNo) {
        List<CardKey> cardKeys = cardKeyService.exportCardKeys(batchNo);
        return Result.success(cardKeys);
    }
} 