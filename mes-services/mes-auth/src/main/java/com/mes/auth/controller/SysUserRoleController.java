package com.mes.auth.controller;

import com.mes.common.core.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户角色关联控制器
 */
@RestController
@RequestMapping("/system/user")
public class SysUserRoleController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取用户已分配的角色ID列表
     */
    @GetMapping("/{userId}/roles")
    public Result<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        String sql = "SELECT role_id FROM sys_user_role WHERE user_id = ?";
        List<Long> roleIds = jdbcTemplate.queryForList(sql, Long.class, userId);
        return Result.success(roleIds);
    }

    /**
     * 分配角色给用户
     */
    @PutMapping("/roles")
    public Result<Void> assignRoles(@RequestBody UserRoleRequest request) {
        Long userId = request.getUserId();
        List<Long> roleIds = request.getRoleIds();

        // 删除原有关联
        String deleteSql = "DELETE FROM sys_user_role WHERE user_id = ?";
        jdbcTemplate.update(deleteSql, userId);

        // 插入新关联
        if (roleIds != null && !roleIds.isEmpty()) {
            String insertSql = "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)";
            for (Long roleId : roleIds) {
                jdbcTemplate.update(insertSql, userId, roleId);
            }
        }
        return Result.success();
    }

    /**
     * 请求体
     */
    public static class UserRoleRequest {
        private Long userId;
        private List<Long> roleIds;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public List<Long> getRoleIds() { return roleIds; }
        public void setRoleIds(List<Long> roleIds) { this.roleIds = roleIds; }
    }
}
