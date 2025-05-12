package com.szr.admin.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 登录结果
 */
@Data
@Builder
public class LoginResult {
    /**
     * 访问令牌
     */
    private String token;
    
    /**
     * 令牌类型
     */
    private String tokenType;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 过期时间（秒）
     */
    private Integer expiresIn;
} 