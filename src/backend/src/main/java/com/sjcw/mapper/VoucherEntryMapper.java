package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.VoucherEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VoucherEntryMapper extends BaseMapper<VoucherEntry> {

    List<VoucherEntry> selectByVoucherId(@Param("voucherId") String voucherId);

    void deleteByVoucherId(@Param("voucherId") String voucherId);
}