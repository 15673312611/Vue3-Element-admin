package com.szr.admin.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 自定义安全表达式根对象
 * 提供admin用户拥有所有权限的逻辑
 */
public class CustomSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public CustomSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    /**
     * 检查权限 - 自定义实现，admin用户拥有所有权限
     */
    public boolean customHasAuthority(String authority) {
        // admin用户拥有所有权限
        if (isAdmin()) {
            return true;
        }
        
        // 原始权限检查
        return hasAuthority(authority);
    }

    /**
     * 检查任一权限 - 自定义实现，admin用户拥有所有权限
     */
    public boolean customHasAnyAuthority(String... authorities) {
        // admin用户拥有所有权限
        if (isAdmin()) {
            return true;
        }
        
        // 原始权限检查
        return hasAnyAuthority(authorities);
    }

    /**
     * 检查角色 - 自定义实现，admin用户拥有所有角色
     */
    public boolean customHasRole(String role) {
        // admin用户拥有所有角色
        if (isAdmin()) {
            return true;
        }
        
        // 原始角色检查
        return hasRole(role);
    }

    /**
     * 检查任一角色 - 自定义实现，admin用户拥有所有角色
     */
    public boolean customHasAnyRole(String... roles) {
        // admin用户拥有所有角色
        if (isAdmin()) {
            return true;
        }
        
        // 原始角色检查
        return hasAnyRole(roles);
    }
    
    /**
     * 判断当前用户是否为admin
     */
    public boolean isAdmin() {
        Authentication authentication = getAuthentication();
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
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(auth -> "ROLE_ADMIN".equals(auth) || "admin".equals(auth));
    }

    @Override
    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    @Override
    public Object getFilterObject() {
        return filterObject;
    }

    @Override
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    @Override
    public Object getReturnObject() {
        return returnObject;
    }

    @Override
    public Object getThis() {
        return target;
    }
    
    public void setThis(Object target) {
        this.target = target;
    }
} 