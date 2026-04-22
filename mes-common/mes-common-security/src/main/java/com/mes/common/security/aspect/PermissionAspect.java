package com.mes.common.security.aspect;

import com.mes.common.security.annotation.Logical;
import com.mes.common.security.annotation.RequiresPermissions;
import com.mes.common.security.annotation.RequiresRoles;
import com.mes.common.security.entity.LoginUser;
import com.mes.common.security.exception.PermissionDeniedException;
import com.mes.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 权限校验切面
 */
@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private TokenService tokenService;

    @Autowired(required = false)
    private HttpServletRequest request;

    /**
     * 权限校验切面
     */
    @Around("@annotation(com.mes.common.security.annotation.RequiresPermissions)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresPermissions annotation = method.getAnnotation(RequiresPermissions.class);

        // 获取当前用户
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            throw new PermissionDeniedException("未登录或登录已过期");
        }

        // 检查权限
        String[] permissions = annotation.value();
        if (permissions.length == 0) {
            return joinPoint.proceed();
        }

        Set<String> userPermissions = loginUser.getPermissions();
        if (userPermissions == null || userPermissions.isEmpty()) {
            throw new PermissionDeniedException("无访问权限");
        }

        // 超级管理员放行
        if (userPermissions.contains("*:*:*")) {
            return joinPoint.proceed();
        }

        boolean hasPermission;
        if (annotation.logical() == Logical.AND) {
            hasPermission = hasAllPermissions(userPermissions, permissions);
        } else {
            hasPermission = hasAnyPermission(userPermissions, permissions);
        }

        if (!hasPermission) {
            throw new PermissionDeniedException("无访问权限: " + String.join(", ", permissions));
        }

        return joinPoint.proceed();
    }

    /**
     * 角色校验切面
     */
    @Around("@annotation(com.mes.common.security.annotation.RequiresRoles)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取注解
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresRoles annotation = method.getAnnotation(RequiresRoles.class);

        // 获取当前用户
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            throw new PermissionDeniedException("未登录或登录已过期");
        }

        // 检查角色
        String[] roles = annotation.value();
        if (roles.length == 0) {
            return joinPoint.proceed();
        }

        Set<String> userRoles = loginUser.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            throw new PermissionDeniedException("无访问权限");
        }

        // 超级管理员角色放行
        if (userRoles.contains("admin") || userRoles.contains("superadmin")) {
            return joinPoint.proceed();
        }

        boolean hasRole;
        if (annotation.logical() == Logical.AND) {
            hasRole = hasAllRoles(userRoles, roles);
        } else {
            hasRole = hasAnyRole(userRoles, roles);
        }

        if (!hasRole) {
            throw new PermissionDeniedException("无访问权限，需要角色: " + String.join(", ", roles));
        }

        return joinPoint.proceed();
    }

    private boolean hasAllPermissions(Set<String> userPermissions, String[] permissions) {
        for (String permission : permissions) {
            if (!userPermissions.contains(permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAnyPermission(Set<String> userPermissions, String[] permissions) {
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAllRoles(Set<String> userRoles, String[] roles) {
        for (String role : roles) {
            if (!userRoles.contains(role)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasAnyRole(Set<String> userRoles, String[] roles) {
        for (String role : roles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
}
