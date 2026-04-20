package com.mes.auth.controller;

import com.mes.auth.mapper.SysMenuMapper;
import com.mes.common.core.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色菜单关联控制器
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleMenuController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SysMenuMapper menuMapper;

    /**
     * 获取角色已分配的菜单ID列表
     */
    @GetMapping("/{roleId}/menu")
    public Result<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        String sql = "SELECT menu_id FROM sys_role_menu WHERE role_id = ?";
        List<Long> menuIds = jdbcTemplate.queryForList(sql, Long.class, roleId);
        return Result.success(menuIds);
    }

    /**
     * 分配菜单权限
     */
    @PutMapping("/menu")
    public Result<Void> assignMenu(@RequestBody RoleMenuRequest request) {
        Long roleId = request.getRoleId();
        List<Long> menuIds = request.getMenuIds();

        // 删除原有关联
        String deleteSql = "DELETE FROM sys_role_menu WHERE role_id = ?";
        jdbcTemplate.update(deleteSql, roleId);

        // 插入新关联
        if (menuIds != null && !menuIds.isEmpty()) {
            String insertSql = "INSERT INTO sys_role_menu (role_id, menu_id) VALUES (?, ?)";
            for (Long menuId : menuIds) {
                jdbcTemplate.update(insertSql, roleId, menuId);
            }
        }
        return Result.success();
    }

    /**
     * 请求体
     */
    public static class RoleMenuRequest {
        private Long roleId;
        private List<Long> menuIds;

        public Long getRoleId() { return roleId; }
        public void setRoleId(Long roleId) { this.roleId = roleId; }
        public List<Long> getMenuIds() { return menuIds; }
        public void setMenuIds(List<Long> menuIds) { this.menuIds = menuIds; }
    }
}