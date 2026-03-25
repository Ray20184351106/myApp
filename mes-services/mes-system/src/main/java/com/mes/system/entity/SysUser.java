package com.mes.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String nickname;
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Long deptId;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
    @TableLogic
    private Integer deleted;
}
