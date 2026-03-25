package com.mes.common.security.service;

import com.mes.common.core.constant.Constants;
import com.mes.common.redis.service.RedisService;
import com.mes.common.security.entity.LoginUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Token 服务
 */
@Component
public class TokenService {

    /** 令牌自定义标识 */
    @Value("${token.header:Authorization}")
    private String header;

    /** 令牌秘钥 */
    @Value("${token.secret:mescloudmescloudmescloudmescloudmescloud}")
    private String secret;

    /** 令牌有效期（默认30分钟） */
    @Value("${token.expireTime:30}")
    private int expireTime;

    /** 令牌前缀 */
    @Value("${token.prefix:Bearer }")
    private String tokenPrefix;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    private static final Long MILLIS_MINUTE_TEN = 20 * 60 * 1000L;

    @Autowired
    private RedisService redisService;

    /** 获取用户身份信息 */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (token != null) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = getTokenKey(uuid);
                return (LoginUser) redisService.get(userKey);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /** 设置用户身份信息 */
    public void setLoginUser(LoginUser loginUser) {
        if (loginUser != null && !"".equals(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /** 删除用户身份信息 */
    public void delLoginUser(String token) {
        if (token != null) {
            String userKey = getTokenKey(token);
            redisService.delete(userKey);
        }
    }

    /** 创建令牌 */
    public String createToken(LoginUser loginUser) {
        String token = UUID.randomUUID().toString().replace("-", "");
        loginUser.setToken(token);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /** 验证令牌有效期，相差不足20分钟，自动刷新缓存 */
    public void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TEN) {
            refreshToken(loginUser);
        }
    }

    /** 刷新令牌有效期 */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据 uuid 将 loginUser 缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisService.set(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /** 获取请求 token */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (token != null && token.startsWith(tokenPrefix)) {
            token = token.replace(tokenPrefix, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return "login_tokens:" + uuid;
    }

    /** 从数据声明生成令牌 */
    private String createToken(Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claims(claims)
                .signWith(key)
                .compact();
    }

    /** 从令牌中获取数据声明 */
    private Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
