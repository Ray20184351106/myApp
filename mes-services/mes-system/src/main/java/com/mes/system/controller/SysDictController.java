package com.mes.system.controller;

import com.mes.common.core.result.Result;
import com.mes.system.entity.SysDict;
import com.mes.system.service.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典管理控制器
 */
@RestController
@RequestMapping("/dict")
public class SysDictController {

    @Autowired
    private SysDictService dictService;

    /**
     * 根据字典类型查询字典数据
     */
    @GetMapping("/type/{dictType}")
    public Result<List<SysDict>> getByType(@PathVariable String dictType) {
        return Result.success(dictService.getByType(dictType));
    }

    /**
     * 获取所有字典类型
     */
    @GetMapping("/types")
    public Result<List<SysDict>> getAllTypes() {
        return Result.success(dictService.getAllDictTypes());
    }

    /**
     * 新增字典
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysDict dict) {
        dictService.save(dict);
        return Result.success();
    }

    /**
     * 修改字典
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysDict dict) {
        dictService.updateById(dict);
        return Result.success();
    }

    /**
     * 删除字典
     */
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        dictService.removeById(id);
        return Result.success();
    }
}
