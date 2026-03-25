package com.mes.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域名
        config.addAllowedOriginPattern("*");
        // 允许的请求头
        config.addAllowedHeader("*");
        // 允许的请求方法
        config.addAllowedMethod("*");
        // 允许携带 cookie
        config.setAllowCredentials(true);
        // 预检请求缓存时间
        config.setMaxAge(3600L);
        // 暴露的响应头
        config.addExposedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
