package com.mes.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mes.system.entity.SysDict;
import com.mes.system.mapper.SysDictMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class SysDictService extends ServiceImpl<SysDictMapper, SysDict> {

    public List<SysDict> getByType(String dictType) {
        return lambdaQuery()
                .eq(SysDict::getDictType, dictType)
                .eq(SysDict::getStatus, 1)
                .orderByAsc(SysDict::getDictSort)
                .list();
    }

    public List<SysDict> getAllDictTypes() {
        return lambdaQuery()
                .select(SysDict::getDictType)
                .groupBy(SysDict::getDictType)
                .list();
    }
}
