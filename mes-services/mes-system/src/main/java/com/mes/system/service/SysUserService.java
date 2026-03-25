package com.mes.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mes.system.entity.SysUser;
import com.mes.system.mapper.SysUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    public Page<SysUser> pageList(int pageNum, int pageSize, String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(username), SysUser::getUsername, username);
        wrapper.eq(SysUser::getDeleted, 0);
        wrapper.orderByDesc(SysUser::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    public SysUser getByUsername(String username) {
        return lambdaQuery()
                .eq(SysUser::getUsername, username)
                .eq(SysUser::getDeleted, 0)
                .one();
    }
}
