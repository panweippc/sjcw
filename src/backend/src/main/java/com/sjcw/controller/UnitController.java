package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.Unit;
import com.sjcw.service.UnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unit")
public class UnitController {

    @Autowired
    private UnitService unitService;

    @GetMapping("/tree")
    public Result<List<Unit>> tree() {
        List<Unit> units = unitService.selectUnitTree();
        return Result.success(units);
    }

    @GetMapping("/list")
    public Result<List<Unit>> list(Unit unit) {
        List<Unit> units = unitService.selectUnitList(unit);
        return Result.success(units);
    }

    @GetMapping("/{id}")
    public Result<Unit> getById(@PathVariable String id) {
        Unit unit = unitService.getById(id);
        return Result.success(unit);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Unit unit) {
        unitService.saveUnit(unit);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Unit unit) {
        unitService.updateUnit(unit);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        unitService.removeUnit(id);
        return Result.success();
    }
}
