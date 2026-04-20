package com.mes.auth.service;

import com.mes.auth.entity.LoginBody;
import com.mes.auth.entity.SellerUser;
import com.mes.auth.mapper.SellerUserMapper;
import com.mes.common.core.result.Result;
import com.mes.common.redis.service.RedisService;
import com.mes.common.security.entity.LoginUser;
import com.mes.common.security.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private SellerUserMapper userMapper;

    @Mock
    private TokenService tokenService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder passwordEncoder;

    private SellerUser testUser;
    private LoginBody loginBody;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        // 创建测试用户
        testUser = new SellerUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setNickname("测试用户");
        testUser.setAvatar("http://example.com/avatar.png");
        testUser.setStatus(1);

        // 创建登录请求
        loginBody = new LoginBody();
        loginBody.setUsername("testuser");
        loginBody.setPassword("password123");
    }

    /**
     * ==================== 登录成功场景 ====================
     */

    @Test
    @DisplayName("登录成功 - 用户名密码正确")
    void testLogin_Success() {
        // Arrange
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("mock-token-123");

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMsg());
        assertEquals("mock-token-123", result.getData());
        verify(userMapper).selectByUsername("testuser");
        verify(tokenService).createToken(any(LoginUser.class));
    }

    /**
     * ==================== 登录失败场景 ====================
     */

    @Test
    @DisplayName("登录失败 - 用户不存在")
    void testLogin_UserNotFound() {
        // Arrange
        when(userMapper.selectByUsername("nonexistent")).thenReturn(null);
        loginBody.setUsername("nonexistent");

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMsg());
        assertNull(result.getData());
        verify(userMapper).selectByUsername("nonexistent");
        verify(tokenService, never()).createToken(any());
    }

    @Test
    @DisplayName("登录失败 - 用户被禁用 (status=0)")
    void testLogin_UserDisabled() {
        // Arrange
        testUser.setStatus(0);
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户已被禁用", result.getMsg());
        assertNull(result.getData());
        verify(tokenService, never()).createToken(any());
    }

    @Test
    @DisplayName("登录失败 - 用户被禁用 (status=2)")
    void testLogin_UserDisabled_Status2() {
        // Arrange
        testUser.setStatus(2);
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户已被禁用", result.getMsg());
        verify(tokenService, never()).createToken(any());
    }

    @Test
    @DisplayName("登录失败 - 密码错误")
    void testLogin_WrongPassword() {
        // Arrange
        loginBody.setPassword("wrongpassword");
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("密码错误", result.getMsg());
        verify(tokenService, never()).createToken(any());
    }

    /**
     * ==================== 边界条件测试 ====================
     */

    @Test
    @DisplayName("登录失败 - 用户名为空")
    void testLogin_EmptyUsername() {
        // Arrange
        loginBody.setUsername("");
        loginBody.setPassword("password123");

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMsg());
    }

    @Test
    @DisplayName("登录失败 - 密码为空")
    void testLogin_EmptyPassword() {
        // Arrange - 空密码会先查询用户，用户存在才会检查密码
        loginBody.setUsername("testuser");
        loginBody.setPassword("");
        when(userMapper.selectByUsername("testuser")).thenReturn(testUser);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("密码错误", result.getMsg());
    }

    @Test
    @DisplayName("登录失败 - 用户名为 null")
    void testLogin_NullUsername() {
        // Arrange
        loginBody.setUsername(null);
        loginBody.setPassword("password123");

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
    }

    @Test
    @DisplayName("登录失败 - 密码为 null")
    void testLogin_NullPassword() {
        // Arrange
        loginBody.setUsername("testuser");
        loginBody.setPassword(null);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
    }

    @Test
    @DisplayName("登录 - 用户名包含特殊字符")
    void testLogin_SpecialCharactersInUsername() {
        // Arrange
        loginBody.setUsername("test'user");
        when(userMapper.selectByUsername("test'user")).thenReturn(null);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        verify(userMapper).selectByUsername("test'user");
    }

    @Test
    @DisplayName("登录 - 用户名包含中文")
    void testLogin_ChineseUsername() {
        // Arrange
        loginBody.setUsername("测试用户");
        when(userMapper.selectByUsername("测试用户")).thenReturn(null);

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        verify(userMapper).selectByUsername("测试用户");
    }

    @Test
    @DisplayName("登录 - 密码包含特殊字符")
    void testLogin_SpecialCharactersInPassword() {
        // Arrange
        SellerUser specialUser = new SellerUser();
        specialUser.setId(2L);
        specialUser.setUsername("specialuser");
        specialUser.setPassword(passwordEncoder.encode("p@ssw0rd!#$%^&*()"));
        specialUser.setStatus(1);

        loginBody.setUsername("specialuser");
        loginBody.setPassword("p@ssw0rd!#$%^&*()");

        when(userMapper.selectByUsername("specialuser")).thenReturn(specialUser);
        when(tokenService.createToken(any(LoginUser.class))).thenReturn("token-special");

        // Act
        Result<?> result = authService.login(loginBody);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("登录成功", result.getMsg());
    }

    /**
     * ==================== 登出测试 ====================
     */

    @Test
    @DisplayName("登出成功")
    void testLogout_Success() {
        // Arrange
        String token = "Bearer test-token-123";

        // Act
        Result<?> result = authService.logout(token);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMsg());
        verify(tokenService).delLoginUser(token);
    }

    @Test
    @DisplayName("登出 - Token 为 null")
    void testLogout_NullToken() {
        // Act
        Result<?> result = authService.logout(null);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        // 注意：实际代码中 null token 也会调用 delLoginUser，但 delLoginUser 内部会检查 null
        // 这里我们验证实际行为
    }

    /**
     * ==================== 获取用户信息测试 ====================
     */

    @Test
    @DisplayName("获取用户信息成功")
    void testGetUserInfo_Success() {
        // Arrange
        SellerUser user = new SellerUser();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setPassword("hashed-password"); // 应该被清除

        when(userMapper.selectById(1L)).thenReturn(user);

        // Act
        Result<SellerUser> result = authService.getUserInfo(1L);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        assertEquals("testuser", result.getData().getUsername());
        assertNull(result.getData().getPassword(), "密码应该被清除");
    }

    @Test
    @DisplayName("获取用户信息 - 用户不存在")
    void testGetUserInfo_UserNotFound() {
        // Arrange
        when(userMapper.selectById(999L)).thenReturn(null);

        // Act
        Result<SellerUser> result = authService.getUserInfo(999L);

        // Assert
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("获取用户信息 - 用户 ID 为 null")
    void testGetUserInfo_NullUserId() {
        // Act & Assert - 实际代码不会抛出异常，但会查询 null ID
        // 这里我们验证实际行为
        Result<SellerUser> result = authService.getUserInfo(null);
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("用户不存在", result.getMsg());
    }
}
