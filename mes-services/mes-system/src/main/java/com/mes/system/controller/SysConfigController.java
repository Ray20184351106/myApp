package com.mes.system.controller;

import com.mes.common.core.result.Result;
import com.mes.system.entity.SysConfig;
import com.mes.system.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/system/config")
public class SysConfigController {

    @Autowired
    private SysConfigService configService;

    /**
     * 根据Key获取配置
     */
    @GetMapping("/key/{configKey}")
    public Result<SysConfig> getByKey(@PathVariable String configKey) {
        return Result.success(configService.getByKey(configKey));
    }

    /**
     * 根据Key获取配置值
     */
    @GetMapping("/value/{configKey}")
    public Result<String> getValue(@PathVariable String configKey) {
        return Result.success(configService.getConfigValue(configKey));
    }

    /**
     * 新增配置
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysConfig config) {
        configService.save(config);
        return Result.success();
    }

    /**
     * 修改配置
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysConfig config) {
        configService.updateById(config);
        return Result.success();
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        configService.removeById(id);
        return Result.success();
    }
}
