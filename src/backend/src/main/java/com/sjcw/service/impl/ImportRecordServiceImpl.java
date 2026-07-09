package com.sjcw.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.ImportRecord;
import com.sjcw.mapper.ImportRecordMapper;
import com.sjcw.service.ImportRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ImportRecordServiceImpl extends ServiceImpl<ImportRecordMapper, ImportRecord> implements ImportRecordService {

    @Override
    public List<ImportRecord> selectImportRecordList(ImportRecord importRecord) {
        return baseMapper.selectImportRecordList(importRecord);
    }

    @Override
    public boolean save(ImportRecord entity) {
        entity.setCreatedTime(LocalDateTime.now());
        return super.save(entity);
    }
}
