package com.mes.auth.controller;

import com.mes.auth.entity.LoginBody;
import com.mes.auth.entity.RegisterBody;
import com.mes.auth.service.AuthService;
import com.mes.common.core.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 登录
     */
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginBody body) {
        return authService.login(body);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterBody body) {
        return authService.register(body);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public Result<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return authService.logout(token);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<?> getInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.error(401, "未登录");
        }
        return authService.getUserInfo(userId);
    }
}
