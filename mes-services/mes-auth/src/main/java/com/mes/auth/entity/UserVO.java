package com.mes.auth.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户视图对象（包含部门信息）
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private Long deptId;
    private String deptName;  // 部门名称
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;
    private Integer deleted;
    private List<SysRole> roles;  // 用户角色列表
}
