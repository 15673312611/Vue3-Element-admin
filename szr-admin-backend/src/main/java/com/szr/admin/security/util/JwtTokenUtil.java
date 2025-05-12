package com.szr.admin.security.util;

import com.szr.admin.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT工具类
 */
@Component
public class JwtTokenUtil {
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 从JWT中获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * 获取JWT的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 从JWT中获取指定的声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims != null ? claimsResolver.apply(claims) : null;
    }
    
    /**
     * 解析JWT获取所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            // 记录异常但不抛出
            return null;
        }
    }
    
    /**
     * 检查JWT是否已过期
     */
    private Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getExpirationDateFromToken(token);
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    /**
     * 生成JWT
     */
    public String generateToken(UserDetails userDetails) {
        try {
            Map<String, Object> claims = new HashMap<>();
            return doGenerateToken(claims, userDetails.getUsername());
        } catch (Exception e) {
            System.err.println("生成token异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 生成JWT
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        try {
            System.out.println("开始生成JWT, 主题: " + subject);
            final Date createdDate = new Date();
            final Date expirationDate = new Date(createdDate.getTime() + jwtConfig.getExpire() * 1000);
            
            System.out.println("JWT密钥长度: " + jwtConfig.getSecret().length());
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(subject)
                    .setIssuedAt(createdDate)
                    .setExpiration(expirationDate)
                    .signWith(key)
                    .compact();
            
            System.out.println("JWT生成成功");
            return token;
        } catch (Exception e) {
            System.err.println("生成JWT异常: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 验证JWT是否有效
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("JWT验证失败: " + e.getMessage());
            return false;
        }
    }
} 