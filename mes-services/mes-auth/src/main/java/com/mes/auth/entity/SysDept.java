package com.mes.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门实体
 */
@Data
@TableName("sys_dept")
public class SysDept {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parentId;
    private String deptName;
    private String deptCode;
    private Integer sort;
    private String leader;
    private String phone;
    private String email;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private List<SysDept> children;
}
