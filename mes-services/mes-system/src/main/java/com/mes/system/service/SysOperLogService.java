package com.mes.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mes.system.entity.SysOperLog;
import com.mes.system.mapper.SysOperLogMapper;
import org.springframework.stereotype.Service;

@Service
public class SysOperLogService extends ServiceImpl<SysOperLogMapper, SysOperLog> {

    public void recordLog(SysOperLog log) {
        save(log);
    }
}
