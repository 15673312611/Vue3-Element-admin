package com.szr.admin.security.filter;

import com.szr.admin.config.JwtConfig;
import com.szr.admin.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT身份验证过滤器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final JwtConfig jwtConfig;
    
    @Autowired
    public JwtAuthenticationFilter(
            @Lazy UserDetailsService userDetailsService,
            JwtTokenUtil jwtTokenUtil,
            JwtConfig jwtConfig) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.jwtConfig = jwtConfig;
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // 登录、登出和静态资源请求不需要JWT验证
        return path.contains("/auth/login") 
            || path.contains("/auth/logout")
            || path.matches("^(/|/index\\.html|/favicon\\.ico|/static/.*)$");
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            // 如果已经有认证信息，不做处理
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }
            
            // 获取JWT令牌
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                try {
                    // 验证JWT令牌有效性
                    if (jwtTokenUtil.validateToken(jwt)) {
                        // 从JWT中获取用户名
                        String username = jwtTokenUtil.getUsernameFromToken(jwt);
                        
                        if (StringUtils.hasText(username)) {
                            try {
                                // 加载用户详情
                                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                                
                                if (userDetails != null) {
                                    // 创建认证信息
                                    UsernamePasswordAuthenticationToken authentication = 
                                            new UsernamePasswordAuthenticationToken(
                                                    userDetails, null, userDetails.getAuthorities());
                                    
                                    authentication.setDetails(
                                            new WebAuthenticationDetailsSource().buildDetails(request));
                                    
                                    // 设置认证信息到上下文
                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                    logger.debug("已为用户'" + username + "'设置安全上下文");
                                }
                            } catch (UsernameNotFoundException e) {
                                logger.error("JWT令牌中的用户不存在", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("JWT验证失败", e);
                }
            }
        } catch (Exception e) {
            logger.error("JWT过滤器异常", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfig.getHeader());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfig.getTokenPrefix() + " ")) {
            return bearerToken.substring(jwtConfig.getTokenPrefix().length() + 1);
        }
        return null;
    }
} 