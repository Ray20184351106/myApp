package com.mes.auth.service;

import com.mes.auth.entity.SysMenu;
import com.mes.auth.mapper.PermissionMapper;
import com.mes.auth.mapper.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 权限服务
 */
@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private SysMenuMapper menuMapper;

    /**
     * 获取用户菜单列表（树形结构）
     */
    public List<SysMenu> selectMenusByUserId(Long userId) {
        List<Long> menuIds = permissionMapper.selectMenuIdsByUserId(userId);
        if (menuIds == null || menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询菜单详情
        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds);

        // 构建树形结构
        return buildMenuTree(menus, 0L);
    }

    /**
     * 获取用户权限标识列表
     */
    public Set<String> selectPermsByUserId(Long userId) {
        List<String> perms = permissionMapper.selectPermsByUserId(userId);
        Set<String> permsSet = new HashSet<>();

        // 处理多权限情况（如：system:user:add,system:user:edit）
        for (String perm : perms) {
            if (perm != null && !perm.isEmpty()) {
                permsSet.addAll(Arrays.asList(perm.split(",")));
            }
        }

        // 添加超级管理员标识
        permsSet.add("*:*:*");

        return permsSet;
    }

    /**
     * 获取用户角色标识列表
     */
    public Set<String> selectRoleKeysByUserId(Long userId) {
        List<String> roleKeys = permissionMapper.selectRoleKeysByUserId(userId);
        return new HashSet<>(roleKeys);
    }

    /**
     * 构建菜单树
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus, Long parentId) {
        List<SysMenu> result = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (parentId.equals(menu.getParentId())) {
                List<SysMenu> children = buildMenuTree(menus, menu.getId());
                menu.setChildren(children.isEmpty() ? null : children);
                result.add(menu);
            }
        }

        // 按排序字段排序
        result.sort(Comparator.comparing(SysMenu::getSort, Comparator.nullsLast(Comparator.naturalOrder())));

        return result;
    }

    /**
     * 获取所有菜单（树形结构）
     */
    public List<SysMenu> selectAllMenus() {
        List<SysMenu> menus = menuMapper.selectList(null);
        return buildMenuTree(menus, 0L);
    }

    /**
     * 获取路由菜单（仅目录和菜单，不含按钮）
     */
    public List<SysMenu> selectRoutersByUserId(Long userId) {
        List<Long> menuIds = permissionMapper.selectMenuIdsByUserId(userId);
        if (menuIds == null || menuIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询菜单详情，过滤掉按钮类型
        List<SysMenu> menus = menuMapper.selectBatchIds(menuIds).stream()
                .filter(m -> !"F".equals(m.getMenuType()))
                .collect(Collectors.toList());

        return buildMenuTree(menus, 0L);
    }
}
