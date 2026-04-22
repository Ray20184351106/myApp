package com.mes.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mes.auth.entity.SysDept;
import com.mes.auth.mapper.SysDeptMapper;
import com.mes.common.core.result.Result;
import com.mes.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController {

    @Autowired
    private SysDeptMapper deptMapper;

    /**
     * 部门列表（树形）
     */
    @GetMapping("/list")
    @RequiresPermissions("system:dept:list")
    public Result<List<SysDept>> list(
            @RequestParam(required = false) String deptName,
            @RequestParam(required = false) Integer status) {

        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(deptName), SysDept::getDeptName, deptName);
        wrapper.eq(status != null, SysDept::getStatus, status);
        wrapper.eq(SysDept::getDeleted, 0);
        wrapper.orderByAsc(SysDept::getSort);

        List<SysDept> depts = deptMapper.selectList(wrapper);
        return Result.success(buildTree(depts));
    }

    /**
     * 部门树（下拉选择用）
     */
    @GetMapping("/tree")
    public Result<List<SysDept>> tree() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getStatus, 1);
        wrapper.eq(SysDept::getDeleted, 0);
        wrapper.orderByAsc(SysDept::getSort);

        List<SysDept> depts = deptMapper.selectList(wrapper);
        return Result.success(buildTree(depts));
    }

    /**
     * 根据 ID 获取部门
     */
    @GetMapping("/{id}")
    @RequiresPermissions("system:dept:query")
    public Result<SysDept> getInfo(@PathVariable Long id) {
        return Result.success(deptMapper.selectById(id));
    }

    /**
     * 新增部门
     */
    @PostMapping
    @RequiresPermissions("system:dept:add")
    public Result<Void> add(@RequestBody SysDept dept) {
        // 校验部门编码唯一性
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptCode, dept.getDeptCode());
        wrapper.eq(SysDept::getDeleted, 0);
        if (deptMapper.selectCount(wrapper) > 0) {
            return Result.error("部门编码已存在");
        }

        // 校验父部门不能是自身
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            return Result.error("父部门不能是自身");
        }

        dept.setDeleted(0);
        dept.setStatus(dept.getStatus() == null ? 1 : dept.getStatus());
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.insert(dept);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @PutMapping
    @RequiresPermissions("system:dept:edit")
    public Result<Void> edit(@RequestBody SysDept dept) {
        // 校验部门编码唯一性（排除自身）
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDept::getDeptCode, dept.getDeptCode());
        wrapper.ne(SysDept::getId, dept.getId());
        wrapper.eq(SysDept::getDeleted, 0);
        if (deptMapper.selectCount(wrapper) > 0) {
            return Result.error("部门编码已存在");
        }

        // 校验父部门不能是自身
        if (dept.getParentId() != null && dept.getParentId().equals(dept.getId())) {
            return Result.error("父部门不能是自身");
        }

        // 校验父部门不能是子部门
        if (hasChild(dept.getId(), dept.getParentId())) {
            return Result.error("父部门不能是子部门");
        }

        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.updateById(dept);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{id}")
    @RequiresPermissions("system:dept:remove")
    public Result<Void> remove(@PathVariable Long id) {
        // 检查是否存在子部门
        int childCount = deptMapper.countChildrenByParentId(id);
        if (childCount > 0) {
            return Result.error("存在子部门，不允许删除");
        }

        // 检查是否存在关联用户
        int userCount = deptMapper.countUsersByDeptId(id);
        if (userCount > 0) {
            return Result.error("部门存在关联用户，不允许删除");
        }

        SysDept dept = new SysDept();
        dept.setId(id);
        dept.setDeleted(1);
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.updateById(dept);
        return Result.success();
    }

    /**
     * 构建树形结构
     */
    private List<SysDept> buildTree(List<SysDept> depts) {
        Map<Long, SysDept> map = depts.stream().collect(Collectors.toMap(SysDept::getId, d -> d));
        List<SysDept> roots = new ArrayList<>();

        for (SysDept dept : depts) {
            if (dept.getParentId() == null || dept.getParentId() == 0) {
                roots.add(dept);
            } else {
                SysDept parent = map.get(dept.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dept);
                }
            }
        }
        return roots;
    }

    /**
     * 检查是否包含子部门（用于校验父部门不能是子部门）
     */
    private boolean hasChild(Long parentId, Long childId) {
        if (childId == null || childId == 0) {
            return false;
        }
        SysDept child = deptMapper.selectById(childId);
        if (child == null) {
            return false;
        }
        if (parentId.equals(child.getParentId())) {
            return true;
        }
        return hasChild(parentId, child.getParentId());
    }
}
