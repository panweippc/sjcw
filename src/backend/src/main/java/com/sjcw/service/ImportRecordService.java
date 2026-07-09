package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.ImportRecord;

import java.util.List;

public interface ImportRecordService extends IService<ImportRecord> {

    List<ImportRecord> selectImportRecordList(ImportRecord importRecord);
}
