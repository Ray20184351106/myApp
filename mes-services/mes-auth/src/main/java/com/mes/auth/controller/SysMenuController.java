package com.mes.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mes.auth.entity.SysMenu;
import com.mes.auth.mapper.SysMenuMapper;
import com.mes.common.core.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private SysMenuMapper menuMapper;

    /**
     * 菜单列表
     */
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return Result.success(menus);
    }

    /**
     * 菜单树（角色授权用）
     */
    @GetMapping("/tree")
    public Result<List<SysMenu>> tree() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, 1);
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> menus = menuMapper.selectList(wrapper);
        return Result.success(buildTree(menus));
    }

    /**
     * 根据ID获取菜单
     */
    @GetMapping("/{id}")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        return Result.success(menuMapper.selectById(id));
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        menu.setStatus(1);
        menu.setVisible(1);
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.insert(menu);
        return Result.success();
    }

    /**
     * 修改菜单
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysMenu menu) {
        menu.setUpdateTime(LocalDateTime.now());
        menuMapper.updateById(menu);
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        menuMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 构建树形结构
     */
    private List<SysMenu> buildTree(List<SysMenu> menus) {
        Map<Long, SysMenu> map = menus.stream().collect(Collectors.toMap(SysMenu::getId, m -> m));
        List<SysMenu> roots = new ArrayList<>();

        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                roots.add(menu);
            } else {
                SysMenu parent = map.get(menu.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(menu);
                }
            }
        }
        return roots;
    }
}
