package com.szr.admin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /**
     * JWT加密密钥
     */
    private String secret;
    
    /**
     * JWT过期时间（秒）
     */
    private Long expire;
    
    /**
     * JWT存储在Header中的名称
     */
    private String header;
    
    /**
     * Token前缀
     */
    private String tokenPrefix;
} 