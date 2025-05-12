package com.szr.admin.security;

import com.szr.admin.entity.AdminUsers;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员用户详情类
 */
public class AdminUserDetails implements UserDetails {
    
    @Getter
    private final AdminUsers adminUsers;
    
    private final List<String> permissions;
    
    public AdminUserDetails(AdminUsers adminUsers, List<String> permissions) {
        this.adminUsers = adminUsers;
        this.permissions = permissions;
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 返回用户权限
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public String getPassword() {
        return adminUsers.getPassword();
    }
    
    @Override
    public String getUsername() {
        return adminUsers.getUsername();
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return adminUsers.getStatus() == 1;
    }
} 