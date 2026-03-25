package com.mes.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_config")
public class SysConfig {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String configName;
    private String configKey;
    private String configValue;
    private Integer configType;
    private String createBy;
    private LocalDateTime createTime;
    private String remark;
}
