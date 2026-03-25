package com.mes.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.mes.common.core.constant.Constants;
import com.mes.common.core.result.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局认证过滤器
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Value("${token.header:Authorization}")
    private String header;

    @Value("${token.prefix:Bearer }")
    private String tokenPrefix;

    private final RedisTemplate redisTemplate;

    private static final List<String> WHITE_LIST = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/captcha"
    );

    public AuthGlobalFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单放行
        if (isWhitePath(path)) {
            return chain.filter(exchange);
        }

        // 获取 token
        String token = getToken(request);
        if (StrUtil.isEmpty(token)) {
            return unauthorized(exchange, "未授权，请先登录");
        }

        // 验证 token
        try {
            String userKey = "login_tokens:" + token;
            Object loginUser = redisTemplate.opsForValue().get(userKey);
            if (loginUser == null) {
                return unauthorized(exchange, "登录已过期，请重新登录");
            }

            // 将用户信息添加到请求头
            ServerHttpRequest newRequest = request.mutate()
                    .header("X-User-Token", token)
                    .build();
            return chain.filter(exchange.mutate().request(newRequest).build());

        } catch (Exception e) {
            return unauthorized(exchange, "认证失败");
        }
    }

    private boolean isWhitePath(String path) {
        for (String white : WHITE_LIST) {
            if (path.contains(white)) {
                return true;
            }
        }
        return false;
    }

    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(header);
        if (StrUtil.isNotEmpty(token) && token.startsWith(tokenPrefix)) {
            return token.replace(tokenPrefix, "");
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Result<?> result = Result.error(HttpStatus.UNAUTHORIZED.value(), message);
        String body = JSON.toJSONString(result);
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
