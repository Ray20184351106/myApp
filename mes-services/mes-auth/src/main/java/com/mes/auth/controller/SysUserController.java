package com.mes.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mes.auth.entity.SellerUser;
import com.mes.auth.mapper.SellerUserMapper;
import com.mes.common.core.result.PageResult;
import com.mes.common.core.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 用户管理控制器
 * 统一由 mes-auth 服务管理用户数据
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SellerUserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询用户
     */
    @GetMapping("/list")
    public Result<PageResult<SellerUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {

        LambdaQueryWrapper<SellerUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SellerUser::getUsername, username);
        wrapper.eq(SellerUser::getDeleted, 0);
        wrapper.orderByDesc(SellerUser::getCreateTime);

        Page<SellerUser> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 清除密码
        page.getRecords().forEach(user -> user.setPassword(null));

        return Result.success(PageResult.build(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    public Result<SellerUser> getInfo(@PathVariable Long id) {
        SellerUser user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PostMapping
    public Result<Void> add(@RequestBody SellerUser user) {
        // 检查用户名是否存在
        SellerUser existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 加密密码
        if (StringUtils.hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            // 默认密码 123456
            user.setPassword(passwordEncoder.encode("123456"));
        }

        user.setDeleted(0);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        userMapper.insert(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SellerUser user) {
        SellerUser existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }

        // 如果修改了密码，需要加密
        if (StringUtils.hasText(user.getPassword()) && !user.getPassword().equals(existUser.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existUser.getPassword());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 删除用户 (逻辑删除)
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        SellerUser user = new SellerUser();
        user.setId(id);
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }
}
