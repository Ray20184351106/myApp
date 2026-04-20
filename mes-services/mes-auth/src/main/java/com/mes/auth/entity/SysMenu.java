package com.mes.auth.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单实体
 */
@Data
@TableName("sys_menu")
public class SysMenu {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String menuName;
    private Long parentId;
    private Integer sort;
    private String path;
    private String component;
    private String menuType; // M-目录 C-菜单 F-按钮
    private Integer visible;
    private Integer status;
    private String perms;
    private String icon;
    private String createBy;
    private LocalDateTime createTime;
    private String updateBy;
    private LocalDateTime updateTime;
    private String remark;

    @TableField(exist = false)
    private List<SysMenu> children;
}
