package com.sjcw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjcw.entity.Voucher;
import com.sjcw.mapper.VoucherMapper;
import com.sjcw.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class VoucherServiceImpl extends ServiceImpl<VoucherMapper, Voucher> implements VoucherService {

    @Autowired
    private VoucherMapper voucherMapper;

    @Override
    public List<Map<String, Object>> getYearSummary(String unitId, Integer year) {
        return baseMapper.selectYearSummary(unitId, year);
    }

    @Override
    public List<Voucher> getMonthDetail(String unitId, Integer year, Integer month, Integer pageNum, Integer pageSize) {
        List<Voucher> all = baseMapper.selectMonthDetail(unitId, year, month);
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, all.size());
        if (start >= all.size()) {
            return new ArrayList<>();
        }
        return all.subList(start, end);
    }

    @Override
    public Integer getMonthTotalCount(String unitId, Integer year, Integer month) {
        return baseMapper.selectTotalCount(unitId, year, month);
    }

    @Override
    public List<Integer> getAvailableYears(String unitId) {
        QueryWrapper<Voucher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unit_id", unitId)
                .select("DISTINCT YEAR(voucher_date) as year")
                .orderByDesc("year");
        List<Voucher> list = list(queryWrapper);
        Set<Integer> years = new HashSet<>();
        for (Voucher v : list) {
            if (v.getVoucherDate() != null) {
                years.add(v.getVoucherDate().getYear());
            }
        }
        return new ArrayList<>(years);
    }

    @Override
    public boolean save(Voucher entity) {
        entity.setCreatedTime(LocalDateTime.now());
        entity.setUpdatedTime(LocalDateTime.now());
        return super.save(entity);
    }

    @Override
    public boolean updateById(Voucher entity) {
        entity.setUpdatedTime(LocalDateTime.now());
        return super.updateById(entity);
    }

    @Override
    @Transactional
    public void deleteByVoucherNo(String voucherNo) {
        QueryWrapper<Voucher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("voucher_no", voucherNo);
        remove(queryWrapper);
    }

    @Override
    public List<Voucher> getEntriesByVoucherNo(String voucherNo) {
        QueryWrapper<Voucher> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("voucher_no", voucherNo)
                .orderByAsc("entry_no");
        return list(queryWrapper);
    }
}