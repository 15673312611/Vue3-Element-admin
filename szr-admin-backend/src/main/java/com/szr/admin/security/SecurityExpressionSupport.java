package com.szr.admin.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * 安全表达式辅助类
 * 提供静态方法用于PreAuthorize注解中直接使用
 */
@Component("securityExpression")
public class SecurityExpressionSupport {

    /**
     * 检查当前用户是否有指定权限，admin用户拥有所有权限
     */
    public boolean hasPermission(String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        // admin用户拥有所有权限
        if (isAdmin(authentication)) {
            return true;
        }
        
        // 检查具体权限
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission) || 
                        authority.equals("*") || 
                        authority.equals("*:*") || 
                        authority.equals("*:*:*"));
    }
    
    /**
     * 判断当前用户是否为admin
     */
    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        
        // 检查用户名
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            if ("admin".equals(username)) {
                return true;
            }
        } else if (principal instanceof String && "admin".equals(principal)) {
            return true;
        }
        
        // 检查角色
        boolean hasAdminRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth) || "admin".equals(auth));
        
        // 如果debug模式或系统还在初始化阶段，也给予所有权限
        if (System.getProperty("debug") != null || System.getProperty("system.init") != null) {
            return true;
        }
        
        return hasAdminRole;
    }
} 