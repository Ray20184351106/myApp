package com.mes.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限 Mapper
 */
@Mapper
public interface PermissionMapper {

    /**
     * 根据用户ID查询菜单ID列表
     */
    @Select("""
        SELECT DISTINCT m.id
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
        AND m.status = 1
        ORDER BY m.sort
        """)
    List<Long> selectMenuIdsByUserId(Long userId);

    /**
     * 根据用户ID查询权限标识列表
     */
    @Select("""
        SELECT DISTINCT m.perms
        FROM sys_menu m
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id
        WHERE ur.user_id = #{userId}
        AND m.status = 1
        AND m.perms IS NOT NULL
        AND m.perms != ''
        """)
    List<String> selectPermsByUserId(Long userId);

    /**
     * 根据用户ID查询角色标识列表
     */
    @Select("""
        SELECT DISTINCT r.role_key
        FROM sys_role r
        INNER JOIN sys_user_role ur ON r.id = ur.role_id
        WHERE ur.user_id = #{userId}
        AND r.status = 1
        """)
    List<String> selectRoleKeysByUserId(Long userId);

    /**
     * 根据角色ID查询菜单ID列表
     */
    @Select("""
        SELECT menu_id
        FROM sys_role_menu
        WHERE role_id = #{roleId}
        """)
    List<Long> selectMenuIdsByRoleId(Long roleId);
}
