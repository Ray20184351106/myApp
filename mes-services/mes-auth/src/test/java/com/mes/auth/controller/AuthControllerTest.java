package com.mes.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.auth.entity.LoginBody;
import com.mes.auth.entity.RegisterBody;
import com.mes.auth.entity.SellerUser;
import com.mes.auth.service.AuthService;
import com.mes.common.core.result.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthController 单元测试（纯 Mock 方式）
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    /**
     * ==================== 登录接口测试 ====================
     */

    @Test
    @DisplayName("登录接口 - 成功")
    void testLogin_Success() throws Exception {
        // Arrange
        LoginBody body = new LoginBody();
        body.setUsername("testuser");
        body.setPassword("password123");

        Result mockResult = Result.success("登录成功", "token-123");
        when(authService.login(any(LoginBody.class))).thenReturn(mockResult);

        // Act
        Result<?> result = authController.login(body);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMsg());
        assertEquals("token-123", result.getData());
        verify(authService).login(body);
    }

    @Test
    @DisplayName("登录接口 - 用户不存在")
    void testLogin_UserNotFound() throws Exception {
        // Arrange
        LoginBody body = new LoginBody();
        body.setUsername("nonexistent");
        body.setPassword("password123");

        Result mockResult = Result.error("用户不存在");
        when(authService.login(any(LoginBody.class))).thenReturn(mockResult);

        // Act
        Result<?> result = authController.login(body);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMsg());
    }

    @Test
    @DisplayName("登录接口 - 密码错误")
    void testLogin_WrongPassword() throws Exception {
        // Arrange
        LoginBody body = new LoginBody();
        body.setUsername("testuser");
        body.setPassword("wrongpassword");

        Result mockResult = Result.error("密码错误");
        when(authService.login(any(LoginBody.class))).thenReturn(mockResult);

        // Act
        Result<?> result = authController.login(body);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("密码错误", result.getMsg());
    }

    /**
     * ==================== 注册接口测试 ====================
     */

    @Test
    @DisplayName("注册接口 - 成功")
    void testRegister_Success() throws Exception {
        // Arrange
        RegisterBody body = new RegisterBody();
        body.setUsername("newuser");
        body.setPassword("password123");
        body.setNickname("新用户");

        Result mockResult = Result.success("注册成功");
        when(authService.register(any(RegisterBody.class))).thenReturn(mockResult);

        // Act
        Result<?> result = authController.register(body);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMsg());
        verify(authService).register(body);
    }

    @Test
    @DisplayName("注册接口 - 用户名已存在")
    void testRegister_UserExists() throws Exception {
        // Arrange
        RegisterBody body = new RegisterBody();
        body.setUsername("existinguser");
        body.setPassword("password123");

        Result mockResult = Result.error("用户名已存在");
        when(authService.register(any(RegisterBody.class))).thenReturn(mockResult);

        // Act
        Result<?> result = authController.register(body);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户名已存在", result.getMsg());
    }

    /**
     * ==================== 登出接口测试 ====================
     */

    @Test
    @DisplayName("登出接口 - 成功")
    void testLogout_Success() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer test-token");

        Result mockResult = Result.success("操作成功");
        when(authService.logout("test-token")).thenReturn(mockResult);

        // Act
        Result<?> result = authController.logout(request);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(authService).logout("test-token");
    }

    @Test
    @DisplayName("登出接口 - 没有 Token")
    void testLogout_NoToken() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        Result mockResult = Result.success("操作成功");
        when(authService.logout(null)).thenReturn(mockResult);

        // Act
        Result<?> result = authController.logout(request);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(authService).logout(null);
    }

    /**
     * ==================== 获取用户信息接口测试 ====================
     */

    @Test
    @DisplayName("获取用户信息接口 - 成功")
    void testGetInfo_Success() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("userId", 1L);

        SellerUser user = new SellerUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("测试用户");

        Result mockResult = Result.success(user);
        when(authService.getUserInfo(1L)).thenReturn(mockResult);

        // Act
        Result<?> result = authController.getInfo(request);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        verify(authService).getUserInfo(1L);
    }

    @Test
    @DisplayName("获取用户信息接口 - 未登录")
    void testGetInfo_NotLoggedIn() throws Exception {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        // 不设置 userId 属性

        // Act
        Result<?> result = authController.getInfo(request);

        // Assert
        assertNotNull(result);
        assertEquals(401, result.getCode());
        assertEquals("未登录", result.getMsg());
        verify(authService, never()).getUserInfo(anyLong());
    }
}
