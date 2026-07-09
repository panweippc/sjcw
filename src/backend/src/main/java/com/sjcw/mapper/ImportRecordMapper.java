package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.ImportRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ImportRecordMapper extends BaseMapper<ImportRecord> {

    List<ImportRecord> selectImportRecordList(ImportRecord importRecord);
}
