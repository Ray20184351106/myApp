package com.mes.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mes.auth.entity.SellerUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SellerUserMapper extends BaseMapper<SellerUser> {

    @Select("SELECT * FROM sys_user WHERE username = #{username} AND deleted = 0")
    SellerUser selectByUsername(@Param("username") String username);
}
