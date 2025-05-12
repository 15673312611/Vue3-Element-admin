package com.szr.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 自定义认证提供者配置
 */
@Configuration
public class WebAuthenticationProviderConfig {

    @Autowired
    @Qualifier("adminUserDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    @Primary
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        // 设置用户详情服务
        provider.setUserDetailsService(userDetailsService);
        // 设置密码编码器
        provider.setPasswordEncoder(passwordEncoder);
        // 隐藏"用户不存在"异常
        provider.setHideUserNotFoundExceptions(true);
        return provider;
    }
} 