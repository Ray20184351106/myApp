package com.mes.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mes.auth.entity.SysRole;
import com.mes.auth.mapper.SysRoleMapper;
import com.mes.common.core.result.PageResult;
import com.mes.common.core.result.Result;
import com.mes.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private SysRoleMapper roleMapper;

    /**
     * 分页查询角色
     */
    @GetMapping("/list")
    @RequiresPermissions("system:role:list")
    public Result<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleName) {

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName);
        wrapper.eq(SysRole::getDeleted, 0);
        wrapper.orderByAsc(SysRole::getSort);

        Page<SysRole> page = roleMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return Result.success(PageResult.build(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /**
     * 获取所有角色（下拉选择用）
     */
    @GetMapping("/all")
    public Result<List<SysRole>> all() {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getStatus, 1);
        wrapper.eq(SysRole::getDeleted, 0);
        wrapper.orderByAsc(SysRole::getSort);
        return Result.success(roleMapper.selectList(wrapper));
    }

    /**
     * 根据ID获取角色
     */
    @GetMapping("/{id}")
    @RequiresPermissions("system:role:query")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        return Result.success(roleMapper.selectById(id));
    }

    /**
     * 新增角色
     */
    @PostMapping
    @RequiresPermissions("system:role:add")
    public Result<Void> add(@RequestBody SysRole role) {
        role.setDeleted(0);
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.insert(role);
        return Result.success();
    }

    /**
     * 修改角色
     */
    @PutMapping
    @RequiresPermissions("system:role:edit")
    public Result<Void> edit(@RequestBody SysRole role) {
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
        return Result.success();
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:role:remove")
    public Result<Void> remove(@PathVariable Long id) {
        SysRole role = new SysRole();
        role.setId(id);
        role.setDeleted(1);
        role.setUpdateTime(LocalDateTime.now());
        roleMapper.updateById(role);
        return Result.success();
    }
}
