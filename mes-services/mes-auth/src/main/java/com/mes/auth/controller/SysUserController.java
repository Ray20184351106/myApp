package com.mes.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mes.auth.entity.SellerUser;
import com.mes.auth.entity.SysDept;
import com.mes.auth.entity.UserVO;
import com.mes.auth.mapper.SellerUserMapper;
import com.mes.auth.mapper.SysDeptMapper;
import com.mes.common.core.result.PageResult;
import com.mes.common.core.result.Result;
import com.mes.common.security.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 * 统一由 mes-auth 服务管理用户数据
 */
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private SellerUserMapper userMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询用户
     */
    @GetMapping("/list")
    @RequiresPermissions("system:user:list")
    public Result<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username) {

        LambdaQueryWrapper<SellerUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SellerUser::getUsername, username);
        wrapper.eq(SellerUser::getDeleted, 0);
        wrapper.orderByDesc(SellerUser::getCreateTime);

        Page<SellerUser> page = userMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);

        // 转换为 UserVO 并填充部门信息
        List<UserVO> voList = page.getRecords().stream().map(user -> {
            UserVO vo = convertToVO(user);
            // 查询部门名称
            if (user.getDeptId() != null) {
                SysDept dept = deptMapper.selectById(user.getDeptId());
                if (dept != null) {
                    vo.setDeptName(dept.getDeptName());
                }
            }
            return vo;
        }).collect(Collectors.toList());

        return Result.success(PageResult.build(voList, page.getTotal(), pageNum, pageSize));
    }

    /**
     * 根据ID获取用户
     */
    @GetMapping("/{id}")
    @RequiresPermissions("system:user:query")
    public Result<UserVO> getInfo(@PathVariable Long id) {
        SellerUser user = userMapper.selectById(id);
        if (user == null) {
            return Result.success(null);
        }

        UserVO vo = convertToVO(user);
        // 查询部门名称
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept != null) {
                vo.setDeptName(dept.getDeptName());
            }
        }
        return Result.success(vo);
    }

    /**
     * 新增用户
     */
    @PostMapping
    @RequiresPermissions("system:user:add")
    public Result<Void> add(@RequestBody SellerUser user) {
        // 检查用户名是否存在
        SellerUser existUser = userMapper.selectByUsername(user.getUsername());
        if (existUser != null) {
            return Result.error("用户名已存在");
        }

        // 验证部门是否存在
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept == null || dept.getDeleted() == 1) {
                return Result.error("所选部门不存在");
            }
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
    @RequiresPermissions("system:user:edit")
    public Result<Void> edit(@RequestBody SellerUser user) {
        SellerUser existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            return Result.error("用户不存在");
        }

        // 验证部门是否存在
        if (user.getDeptId() != null) {
            SysDept dept = deptMapper.selectById(user.getDeptId());
            if (dept == null || dept.getDeleted() == 1) {
                return Result.error("所选部门不存在");
            }
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
    @RequiresPermissions("system:user:remove")
    public Result<Void> remove(@PathVariable Long id) {
        SellerUser user = new SellerUser();
        user.setId(id);
        user.setDeleted(1);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
        return Result.success();
    }

    /**
     * 转换为 VO
     */
    private UserVO convertToVO(SellerUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());
        vo.setDeptId(user.getDeptId());
        vo.setCreateBy(user.getCreateBy());
        vo.setCreateTime(user.getCreateTime());
        vo.setUpdateBy(user.getUpdateBy());
        vo.setUpdateTime(user.getUpdateTime());
        vo.setRemark(user.getRemark());
        vo.setDeleted(user.getDeleted());
        return vo;
    }
}
