package com.mes.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mes.common.core.result.PageResult;
import com.mes.common.core.result.Result;
import com.mes.system.entity.SysUser;
import com.mes.system.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SysUserService userService;

    /**
     * 分页查询用户
     */
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {
        Page<SysUser> page = userService.pageList(pageNum, pageSize, username);
        return Result.success(PageResult.build(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        SysUser user = userService.getById(id);
        if (user != null) {
            user.setPassword(null); // 清除密码
        }
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        userService.save(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysUser user) {
        userService.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}
