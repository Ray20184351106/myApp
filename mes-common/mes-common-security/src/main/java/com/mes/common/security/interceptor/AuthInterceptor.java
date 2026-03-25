package com.mes.common.security.interceptor;

import com.mes.common.core.constant.Constants;
import com.mes.common.core.exception.ServiceException;
import com.mes.common.security.entity.LoginUser;
import com.mes.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 */
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenService tokenService;

    public AuthInterceptor(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // OPTIONS 请求直接放行
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            throw new ServiceException("未授权，请先登录", 401);
        }

        // 验证 token 有效期
        tokenService.verifyToken(loginUser);

        // 将用户信息存入请求属性
        request.setAttribute(Constants.LOGIN_USER_KEY, loginUser);
        return true;
    }
}
