package com.mes.auth.controller;

import com.mes.auth.entity.SysMenu;
import com.mes.auth.service.PermissionService;
import com.mes.common.core.result.Result;
import com.mes.common.security.entity.LoginUser;
import com.mes.common.security.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 用户权限信息控制器
 */
@RestController
@RequestMapping("/auth")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 获取用户信息（包含权限和角色）
     */
    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("user", loginUser);
        data.put("roles", loginUser.getRoles() != null ? loginUser.getRoles() : Collections.emptySet());
        data.put("permissions", loginUser.getPermissions() != null ? loginUser.getPermissions() : Collections.emptySet());

        return Result.success(data);
    }

    /**
     * 获取用户路由菜单
     */
    @GetMapping("/getRouters")
    public Result<List<SysMenu>> getRouters(HttpServletRequest request) {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }

        List<SysMenu> menus = permissionService.selectRoutersByUserId(loginUser.getUserId());
        return Result.success(menus);
    }
}
