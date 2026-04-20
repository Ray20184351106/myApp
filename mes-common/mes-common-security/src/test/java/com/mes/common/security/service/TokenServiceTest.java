package com.mes.common.security.service;

import com.mes.common.core.constant.Constants;
import com.mes.common.redis.service.RedisService;
import com.mes.common.security.entity.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * TokenService 单元测试
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private RedisService redisService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TokenService tokenService;

    private LoginUser testLoginUser;

    @BeforeEach
    void setUp() {
        // 设置配置值
        ReflectionTestUtils.setField(tokenService, "secret", "mescloudmescloudmescloudmescloudmescloud");
        ReflectionTestUtils.setField(tokenService, "expireTime", 30);
        ReflectionTestUtils.setField(tokenService, "header", "Authorization");
        ReflectionTestUtils.setField(tokenService, "tokenPrefix", "Bearer ");

        // 创建测试用户
        testLoginUser = new LoginUser();
        testLoginUser.setUserId(1L);
        testLoginUser.setUsername("testuser");
        testLoginUser.setNickname("测试用户");
        testLoginUser.setPermissions(Set.of("user:read", "user:write"));
        testLoginUser.setRoles(Set.of("admin"));
    }

    /**
     * ==================== Token 创建测试 ====================
     */

    @Test
    @DisplayName("创建 Token - 成功")
    void testCreateToken_Success() {
        // Arrange
        doNothing().when(redisService).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        // Act
        String token = tokenService.createToken(testLoginUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        // Token 是 JWT 格式，而 loginUser.getToken() 是 UUID，两者不相等
        // 这是预期行为，因为 createToken 返回 JWT，而 loginUser.setToken 设置的是 UUID
        verify(redisService).set(anyString(), eq(testLoginUser), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("创建 Token - 用户信息为空")
    void testCreateToken_NullUser() {
        // Act & Assert
        assertThrows(Exception.class, () -> {
            tokenService.createToken(null);
        });
    }

    /**
     * ==================== Token 解析测试 ====================
     */

    @Test
    @DisplayName("获取登录用户 - 成功")
    void testGetLoginUser_Success() {
        // Arrange
        String token = "test-token-uuid";

        // 创建一个有效的 JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        SecretKey key = Keys.hmacShaKeyFor("mescloudmescloudmescloudmescloudmescloud".getBytes(StandardCharsets.UTF_8));
        String jwt = Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();

        LoginUser cachedUser = new LoginUser();
        cachedUser.setUserId(1L);
        cachedUser.setUsername("testuser");

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(redisService.get("login_tokens:" + token)).thenReturn(cachedUser);

        // Act
        LoginUser result = tokenService.getLoginUser(request);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals("testuser", result.getUsername());
        verify(redisService).get("login_tokens:" + token);
    }

    @Test
    @DisplayName("获取登录用户 - Token 为空")
    void testGetLoginUser_EmptyToken() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        LoginUser result = tokenService.getLoginUser(request);

        // Assert
        assertNull(result);
        verify(redisService, never()).get(anyString());
    }

    @Test
    @DisplayName("获取登录用户 - Redis 中无缓存")
    void testGetLoginUser_NotInRedis() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");

        // Act
        LoginUser result = tokenService.getLoginUser(request);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("获取登录用户 - Token 格式错误")
    void testGetLoginUser_InvalidTokenFormat() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidFormat");

        // Act
        LoginUser result = tokenService.getLoginUser(request);

        // Assert
        assertNull(result);
        // Token 格式错误时会抛出异常，然后被捕获返回 null
    }

    @Test
    @DisplayName("获取 Token - 验证 Redis 查询")
    void testGetToken_VerifyRedisQuery() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        // Act
        String token = tokenService.getToken(request);

        // Assert
        assertEquals("valid-token", token);
    }

    /**
     * ==================== Token 删除测试 ====================
     */

    @Test
    @DisplayName("删除 Token - 成功")
    void testDelLoginUser_Success() {
        // Arrange
        String token = "test-token";
        when(redisService.delete("login_tokens:" + token)).thenReturn(true);

        // Act
        tokenService.delLoginUser(token);

        // Assert
        verify(redisService).delete("login_tokens:" + token);
    }

    @Test
    @DisplayName("删除 Token - Token 为 null")
    void testDelLoginUser_NullToken() {
        // Act
        tokenService.delLoginUser(null);

        // Assert
        verify(redisService, never()).delete(anyString());
    }

    /**
     * ==================== Token 刷新测试 ====================
     */

    @Test
    @DisplayName("刷新 Token - 成功")
    void testRefreshToken_Success() {
        // Arrange
        testLoginUser.setToken("test-token");
        doNothing().when(redisService).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        // Act
        tokenService.refreshToken(testLoginUser);

        // Assert
        assertNotNull(testLoginUser.getLoginTime());
        assertNotNull(testLoginUser.getExpireTime());
        assertTrue(testLoginUser.getExpireTime() > testLoginUser.getLoginTime());
        verify(redisService).set(anyString(), eq(testLoginUser), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("刷新 Token - 用户为空")
    void testRefreshToken_NullUser() {
        // Act & Assert - 实际代码会抛出 NPE，因为 refreshToken 没有检查 null
        assertThrows(NullPointerException.class, () -> {
            tokenService.refreshToken(null);
        });
    }

    @Test
    @DisplayName("验证 Token - 需要刷新")
    void testVerifyToken_NeedRefresh() {
        // Arrange
        testLoginUser.setToken("test-token");
        testLoginUser.setLoginTime(System.currentTimeMillis() - 25 * 60 * 1000L); // 25 分钟前
        testLoginUser.setExpireTime(testLoginUser.getLoginTime() + 30 * 60 * 1000L);

        doNothing().when(redisService).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        // Act
        tokenService.verifyToken(testLoginUser);

        // Assert
        verify(redisService).set(anyString(), eq(testLoginUser), eq(30L), eq(TimeUnit.MINUTES));
    }

    @Test
    @DisplayName("验证 Token - 不需要刷新")
    void testVerifyToken_NoNeedRefresh() {
        // Arrange
        testLoginUser.setToken("test-token");
        testLoginUser.setLoginTime(System.currentTimeMillis());
        testLoginUser.setExpireTime(testLoginUser.getLoginTime() + 30 * 60 * 1000L);

        // Act
        tokenService.verifyToken(testLoginUser);

        // Assert
        verify(redisService, never()).set(anyString(), any(), anyLong(), any(TimeUnit.class));
    }

    /**
     * ==================== Token 获取测试 ====================
     */

    @Test
    @DisplayName("获取 Token - 标准格式")
    void testGetToken_StandardFormat() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

        // Act
        String token = tokenService.getToken(request);

        // Assert
        assertEquals("valid-token", token);
    }

    @Test
    @DisplayName("获取 Token - 没有前缀")
    void testGetToken_NoPrefix() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("no-prefix-token");

        // Act
        String token = tokenService.getToken(request);

        // Assert
        assertEquals("no-prefix-token", token);
    }

    @Test
    @DisplayName("获取 Token - Header 为空")
    void testGetToken_EmptyHeader() {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        String token = tokenService.getToken(request);

        // Assert
        assertNull(token);
    }

    /**
     * ==================== 边界条件测试 ====================
     */

    @Test
    @DisplayName("创建 Token - 用户信息包含特殊字符")
    void testCreateToken_SpecialCharacters() {
        // Arrange
        testLoginUser.setUsername("test'user");
        testLoginUser.setNickname("测试@用户");
        doNothing().when(redisService).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        // Act
        String token = tokenService.createToken(testLoginUser);

        // Assert
        assertNotNull(token);
    }

    @Test
    @DisplayName("创建 Token - 大量权限")
    void testCreateToken_ManyPermissions() {
        // Arrange
        Set<String> manyPermissions = Set.of(
            "user:read", "user:write", "user:delete",
            "order:read", "order:write", "order:delete",
            "product:read", "product:write", "product:delete",
            "admin:all"
        );
        testLoginUser.setPermissions(manyPermissions);
        doNothing().when(redisService).set(anyString(), any(), anyLong(), any(TimeUnit.class));

        // Act
        String token = tokenService.createToken(testLoginUser);

        // Assert
        assertNotNull(token);
    }
}
