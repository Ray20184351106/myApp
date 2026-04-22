package com.mes.auth.service;

import com.mes.auth.entity.LoginBody;
import com.mes.auth.entity.RegisterBody;
import com.mes.auth.entity.SellerUser;
import com.mes.auth.entity.SysMenu;
import com.mes.auth.mapper.SellerUserMapper;
import com.mes.common.core.result.Result;
import com.mes.common.redis.service.RedisService;
import com.mes.common.security.entity.LoginUser;
import com.mes.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private SellerUserMapper userMapper;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PermissionService permissionService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 登录
     */
    public Result<?> login(LoginBody body) {
        // 查询用户
        SellerUser user = userMapper.selectByUsername(body.getUsername());
        if (user == null) {
                return Result.error("用户不存在");
        }

        if (user.getStatus() != 1) {
                return Result.error("用户已被禁用");
        }

        // 验证密码
        if (!passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }

        // 生成登录用户信息
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());
        loginUser.setAvatar(user.getAvatar());

        // 加载用户权限和角色
        Set<String> permissions = permissionService.selectPermsByUserId(user.getId());
        Set<String> roles = permissionService.selectRoleKeysByUserId(user.getId());
        loginUser.setPermissions(permissions);
        loginUser.setRoles(roles);

        // 生成 Token
        String token = tokenService.createToken(loginUser);

        return Result.success("登录成功", token);
    }

    /**
     * 注册
     */
    public Result<?> register(RegisterBody body) {
        // 检查用户名是否存在
        SellerUser existUser = userMapper.selectByUsername(body.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 创建用户
        SellerUser user = new SellerUser();
        user.setUsername(body.getUsername());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user.setNickname(body.getNickname());
        user.setStatus(1);
        user.setCreateTime(java.time.LocalDateTime.now());

        userMapper.insert(user);

        return Result.success("注册成功");
    }

    /**
     * 登出
     */
    public Result<?> logout(String token) {
        tokenService.delLoginUser(token);
        return Result.success("登出成功");
    }

    /**
     * 获取用户信息
     */
    public Result<SellerUser> getUserInfo(Long userId) {
        SellerUser user = userMapper.selectById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 清除敏感信息
        user.setPassword(null);
        return Result.success(user);
    }
}
