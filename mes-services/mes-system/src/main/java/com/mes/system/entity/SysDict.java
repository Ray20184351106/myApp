package com.mes.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_dict_data")
public class SysDict {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer dictSort;
    private String dictLabel;
    private String dictValue;
    private String dictType;
    private Integer status;
    private String createBy;
    private LocalDateTime createTime;
    private String remark;
}
