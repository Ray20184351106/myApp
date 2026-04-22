package com.mes.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mes.auth.entity.SysDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门 Mapper
 */
@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /**
     * 根据用户ID查询部门
     */
    @Select("SELECT d.* FROM sys_dept d " +
            "LEFT JOIN sys_user u ON u.dept_id = d.id " +
            "WHERE u.id = #{userId} AND d.status = 1")
    SysDept selectDeptByUserId(@Param("userId") Long userId);

    /**
     * 查询部门下的用户数量
     */
    @Select("SELECT COUNT(*) FROM sys_user WHERE dept_id = #{deptId} AND deleted = 0")
    int countUsersByDeptId(@Param("deptId") Long deptId);

    /**
     * 查询子部门数量
     */
    @Select("SELECT COUNT(*) FROM sys_dept WHERE parent_id = #{deptId} AND deleted = 0")
    int countChildrenByParentId(@Param("deptId") Long deptId);
}
