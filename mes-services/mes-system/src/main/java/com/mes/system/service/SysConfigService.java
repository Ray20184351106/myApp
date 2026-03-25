package com.mes.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mes.system.entity.SysConfig;
import com.mes.system.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

@Service
public class SysConfigService extends ServiceImpl<SysConfigMapper, SysConfig> {

    public SysConfig getByKey(String configKey) {
        return lambdaQuery()
                .eq(SysConfig::getConfigKey, configKey)
                .one();
    }

    public String getConfigValue(String configKey) {
        SysConfig config = getByKey(configKey);
        return config != null ? config.getConfigValue() : null;
    }
}
