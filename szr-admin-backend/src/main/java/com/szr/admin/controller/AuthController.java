package com.szr.admin.controller;

import com.szr.admin.common.api.Result;
import com.szr.admin.config.JwtConfig;
import com.szr.admin.dto.LoginParam;
import com.szr.admin.dto.LoginResult;
import com.szr.admin.security.AdminUserDetails;
import com.szr.admin.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthenticationProvider authenticationProvider;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;
    
    @Autowired
    public AuthController(
            @Lazy AuthenticationProvider authenticationProvider,
            JwtTokenUtil jwtTokenUtil,
            JwtConfig jwtConfig) {
        this.authenticationProvider = authenticationProvider;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtConfig = jwtConfig;
    }
    
    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<LoginResult> login(@Validated @RequestBody LoginParam loginParam) {
        try {
            // 创建认证令牌
            UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(loginParam.getUsername(), loginParam.getPassword());
            
            // 进行身份验证
            Authentication authentication = authenticationProvider.authenticate(authenticationToken);
            
            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 获取登录用户信息
            AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
            
            // 生成JWT
            String token = jwtTokenUtil.generateToken(userDetails);
            
            if (token == null) {
                return Result.failed("生成令牌失败");
            }
            
            // 返回登录结果
            LoginResult loginResult = LoginResult.builder()
                    .token(token)
                    .tokenType(jwtConfig.getTokenPrefix())
                    .expiresIn(jwtConfig.getExpire().intValue())
                    .build();
            
            return Result.success(loginResult);
        } catch (BadCredentialsException e) {
            return Result.failed("用户名或密码错误");
        } catch (Exception e) {
            return Result.failed("登录失败");
        }
    }
    
    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getUserInfo() {
        try {
            // 获取当前认证用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof AdminUserDetails) {
                AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
                
                // 构建用户信息
                Map<String, Object> data = new HashMap<>();
                data.put("user", userDetails.getAdminUsers());
                data.put("permissions", userDetails.getAuthorities());
                
                return Result.success(data);
            }
            return Result.unauthorized();
        } catch (Exception e) {
            return Result.failed("获取用户信息失败");
        }
    }
    
    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // 清除认证信息
        SecurityContextHolder.clearContext();
        return Result.success();
    }
} 