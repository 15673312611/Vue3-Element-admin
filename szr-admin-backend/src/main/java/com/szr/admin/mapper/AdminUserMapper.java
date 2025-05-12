package com.szr.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szr.admin.entity.AdminUsers;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员用户Mapper接口
 */
public interface AdminUserMapper extends BaseMapper<AdminUsers> {
    
    /**
     * 获取用户权限列表
     */
    @Select("SELECT DISTINCT m.permission FROM admin_users u " +
            "JOIN admin_user_role ur ON u.id = ur.admin_id " +
            "JOIN admin_role_menu rm ON ur.role_id = rm.role_id " +
            "JOIN admin_menus m ON rm.menu_id = m.id " +
            "WHERE u.username = #{username} AND m.permission IS NOT NULL AND m.permission != ''")
    List<String> getUserPermissions(@Param("username") String username);

    /**
     * 获取系统中所有权限列表
     */
    @Select("SELECT DISTINCT permission FROM admin_menus WHERE permission IS NOT NULL AND permission != ''")
    List<String> getAllPermissions();
} 