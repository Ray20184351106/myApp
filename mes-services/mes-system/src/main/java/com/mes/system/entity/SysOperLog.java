package com.mes.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_oper_log")
public class SysOperLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private Integer businessType;
    private String method;
    private String requestMethod;
    private String operName;
    private String deptName;
    private String operUrl;
    private String operIp;
    private String operParam;
    private String jsonResult;
    private Integer status;
    private String errorMsg;
    private LocalDateTime operTime;
    private Long costTime;
}
