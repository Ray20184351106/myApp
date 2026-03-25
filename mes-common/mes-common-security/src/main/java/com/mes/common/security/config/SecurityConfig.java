package com.mes.common.security.config;

import com.mes.common.security.interceptor.AuthInterceptor;
import com.mes.common.security.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 安全配置
 */
@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    @Autowired
    private TokenService tokenService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor(tokenService))
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/register",
                        "/login",
                        "/register",
                        "/captcha",
                        "/actuator/**",
                        "/error",
                        "/doc.html",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/v3/api-docs/**"
                );
    }
}
