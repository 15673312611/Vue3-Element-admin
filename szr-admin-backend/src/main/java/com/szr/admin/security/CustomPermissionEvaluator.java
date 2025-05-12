package com.szr.admin.security;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 自定义权限评估器
 * 扩展Spring Security默认权限评估，支持admin用户拥有所有权限
 */
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || permission == null || !(permission instanceof String)) {
            return false;
        }
        
        // 检查用户名是否为admin或角色中包含admin
        if (isAdminUser(authentication)) {
            return true;
        }
        
        // 常规权限检查
        String permissionString = (String) permission;
        return hasAuthority(authentication, permissionString);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || permission == null || !(permission instanceof String)) {
            return false;
        }
        
        // admin用户直接授权
        if (isAdminUser(authentication)) {
            return true;
        }
        
        // 常规权限检查
        String permissionString = (String) permission;
        return hasAuthority(authentication, permissionString);
    }
    
    /**
     * 检查是否为admin用户
     */
    private boolean isAdminUser(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        
        Object principal = authentication.getPrincipal();
        
        // 检查用户名
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            if ("admin".equals(username)) {
                return true;
            }
        } else if (principal instanceof String && "admin".equals(principal)) {
            return true;
        }
        
        // 检查角色
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority) || "admin".equals(authority));
    }
    
    /**
     * 检查是否拥有指定权限
     */
    private boolean hasAuthority(Authentication authentication, String permission) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(permission) || 
                        authority.equals("*") || 
                        authority.equals("*:*") || 
                        authority.equals("*:*:*"));
    }
} 