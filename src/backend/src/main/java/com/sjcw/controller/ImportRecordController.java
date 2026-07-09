package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.ImportRecord;
import com.sjcw.service.ImportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/import")
public class ImportRecordController {

    @Autowired
    private ImportRecordService importRecordService;

    @GetMapping("/list")
    public Result<List<ImportRecord>> list(ImportRecord importRecord) {
        List<ImportRecord> records = importRecordService.selectImportRecordList(importRecord);
        return Result.success(records);
    }

    @GetMapping("/{id}")
    public Result<ImportRecord> getById(@PathVariable String id) {
        ImportRecord record = importRecordService.getById(id);
        return Result.success(record);
    }

    @PostMapping
    public Result<Void> save(@RequestBody ImportRecord importRecord) {
        importRecordService.save(importRecord);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        importRecordService.removeById(id);
        return Result.success();
    }
}
