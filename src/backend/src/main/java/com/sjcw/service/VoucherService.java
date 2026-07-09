package com.sjcw.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjcw.entity.Voucher;

import java.util.List;
import java.util.Map;

public interface VoucherService extends IService<Voucher> {

    List<Map<String, Object>> getYearSummary(String unitId, Integer year);

    List<Voucher> getMonthDetail(String unitId, Integer year, Integer month, Integer pageNum, Integer pageSize);

    Integer getMonthTotalCount(String unitId, Integer year, Integer month);

    List<Integer> getAvailableYears(String unitId);

    void deleteByVoucherNo(String voucherNo);

    List<Voucher> getEntriesByVoucherNo(String voucherNo);
}