package com.mes.auth.controller;

import com.mes.common.core.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户角色关联控制器单元测试
 */
class SysUserRoleControllerTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private SysUserRoleController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserRoleIds_Success() {
        // 准备测试数据
        Long userId = 1L;
        List<Long> expectedRoleIds = Arrays.asList(1L, 2L, 3L);

        // Mock JdbcTemplate 行为
        when(jdbcTemplate.queryForList(anyString(), eq(Long.class), eq(userId)))
                .thenReturn(expectedRoleIds);

        // 执行测试
        Result<List<Long>> result = controller.getUserRoleIds(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(expectedRoleIds, result.getData());

        // 验证方法调用
        verify(jdbcTemplate, times(1)).queryForList(
                "SELECT role_id FROM sys_user_role WHERE user_id = ?",
                Long.class,
                userId
        );
    }

    @Test
    void testGetUserRoleIds_EmptyResult() {
        // 准备测试数据
        Long userId = 999L;

        // Mock JdbcTemplate 行为 - 返回空列表
        when(jdbcTemplate.queryForList(anyString(), eq(Long.class), eq(userId)))
                .thenReturn(Collections.emptyList());

        // 执行测试
        Result<List<Long>> result = controller.getUserRoleIds(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void testAssignRoles_Success() {
        // 准备测试数据
        Long userId = 1L;
        List<Long> roleIds = Arrays.asList(1L, 2L);

        SysUserRoleController.UserRoleRequest request = new SysUserRoleController.UserRoleRequest();
        request.setUserId(userId);
        request.setRoleIds(roleIds);

        // Mock JdbcTemplate 行为
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        // 执行测试
        Result<Void> result = controller.assignRoles(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证删除旧关联
        verify(jdbcTemplate, times(1)).update(
                "DELETE FROM sys_user_role WHERE user_id = ?",
                userId
        );

        // 验证插入新关联（每个角色一次）
        verify(jdbcTemplate, times(2)).update(
                eq("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)"),
                eq(userId),
                anyLong()
        );
    }

    @Test
    void testAssignRoles_EmptyRoleIds() {
        // 准备测试数据
        Long userId = 1L;

        SysUserRoleController.UserRoleRequest request = new SysUserRoleController.UserRoleRequest();
        request.setUserId(userId);
        request.setRoleIds(Collections.emptyList());

        // Mock JdbcTemplate 行为
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        // 执行测试
        Result<Void> result = controller.assignRoles(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证只删除，不插入
        verify(jdbcTemplate, times(1)).update(
                "DELETE FROM sys_user_role WHERE user_id = ?",
                userId
        );
        verify(jdbcTemplate, never()).update(
                eq("INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)"),
                anyLong(),
                anyLong()
        );
    }

    @Test
    void testAssignRoles_NullRoleIds() {
        // 准备测试数据
        Long userId = 1L;

        SysUserRoleController.UserRoleRequest request = new SysUserRoleController.UserRoleRequest();
        request.setUserId(userId);
        request.setRoleIds(null);

        // Mock JdbcTemplate 行为
        when(jdbcTemplate.update(anyString(), anyLong())).thenReturn(1);

        // 执行测试
        Result<Void> result = controller.assignRoles(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证只删除，不插入
        verify(jdbcTemplate, times(1)).update(
                "DELETE FROM sys_user_role WHERE user_id = ?",
                userId
        );
    }

    @Test
    void testUserRoleRequest_GettersAndSetters() {
        // 测试请求体的 getter 和 setter
        SysUserRoleController.UserRoleRequest request = new SysUserRoleController.UserRoleRequest();

        Long userId = 1L;
        List<Long> roleIds = Arrays.asList(1L, 2L);

        request.setUserId(userId);
        request.setRoleIds(roleIds);

        assertEquals(userId, request.getUserId());
        assertEquals(roleIds, request.getRoleIds());
    }
}
