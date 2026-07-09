package com.sjcw.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sjcw.entity.Voucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface VoucherMapper extends BaseMapper<Voucher> {

    List<Map<String, Object>> selectYearSummary(@Param("unitId") String unitId, @Param("year") Integer year);

    List<Voucher> selectMonthDetail(@Param("unitId") String unitId, @Param("year") Integer year, @Param("month") Integer month);

    Integer selectTotalCount(@Param("unitId") String unitId, @Param("year") Integer year, @Param("month") Integer month);
}