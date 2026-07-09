package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.Voucher;
import com.sjcw.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    @PostMapping("/with-entries")
    public Result<Void> saveWithEntries(@RequestBody Map<String, Object> data) {
        List<Map<String, Object>> entries = (List<Map<String, Object>>) data.get("entries");
        if (entries != null && !entries.isEmpty()) {
            for (int i = 0; i < entries.size(); i++) {
                Map<String, Object> entryData = entries.get(i);
                Voucher voucher = new Voucher();
                voucher.setUnitId((String) data.get("unitId"));
                voucher.setVoucherNo((String) data.get("voucherNo"));
                voucher.setVoucherDate(java.time.LocalDate.parse((String) data.get("voucherDate")));
                voucher.setSummary((String) data.get("summary"));
                voucher.setCreator((String) data.get("creator"));
                voucher.setAuditor((String) data.get("auditor"));
                voucher.setBookkeeper((String) data.get("bookkeeper"));
                voucher.setCashier((String) data.get("cashier"));
                voucher.setStatus((String) data.get("status"));
                voucher.setEntryNo(i + 1);
                
                Object faDebit = entryData.get("faDebitAmount");
                Object faCredit = entryData.get("faCreditAmount");
                Object baDebit = entryData.get("baDebitAmount");
                Object baCredit = entryData.get("baCreditAmount");
                
                voucher.setFaAccountCode((String) entryData.get("faAccountCode"));
                voucher.setFaAccountName((String) entryData.get("faAccountName"));
                voucher.setFaDebitAmount(faDebit != null ? new BigDecimal(faDebit.toString()) : BigDecimal.ZERO);
                voucher.setFaCreditAmount(faCredit != null ? new BigDecimal(faCredit.toString()) : BigDecimal.ZERO);
                voucher.setBaAccountCode((String) entryData.get("baAccountCode"));
                voucher.setBaAccountName((String) entryData.get("baAccountName"));
                voucher.setBaDebitAmount(baDebit != null ? new BigDecimal(baDebit.toString()) : BigDecimal.ZERO);
                voucher.setBaCreditAmount(baCredit != null ? new BigDecimal(baCredit.toString()) : BigDecimal.ZERO);
                
                voucher.setDebitAmount(voucher.getFaDebitAmount().add(voucher.getBaDebitAmount()));
                voucher.setCreditAmount(voucher.getFaCreditAmount().add(voucher.getBaCreditAmount()));
                
                voucherService.save(voucher);
            }
        }
        return Result.success();
    }

    @PutMapping("/with-entries")
    public Result<Void> updateWithEntries(@RequestBody Map<String, Object> data) {
        String voucherId = (String) data.get("voucherId");
        voucherService.deleteByVoucherNo((String) data.get("voucherNo"));
        
        List<Map<String, Object>> entries = (List<Map<String, Object>>) data.get("entries");
        if (entries != null && !entries.isEmpty()) {
            for (int i = 0; i < entries.size(); i++) {
                Map<String, Object> entryData = entries.get(i);
                Voucher voucher = new Voucher();
                voucher.setUnitId((String) data.get("unitId"));
                voucher.setVoucherNo((String) data.get("voucherNo"));
                voucher.setVoucherDate(java.time.LocalDate.parse((String) data.get("voucherDate")));
                voucher.setSummary((String) data.get("summary"));
                voucher.setCreator((String) data.get("creator"));
                voucher.setAuditor((String) data.get("auditor"));
                voucher.setBookkeeper((String) data.get("bookkeeper"));
                voucher.setCashier((String) data.get("cashier"));
                voucher.setStatus((String) data.get("status"));
                voucher.setEntryNo(i + 1);
                
                Object faDebit = entryData.get("faDebitAmount");
                Object faCredit = entryData.get("faCreditAmount");
                Object baDebit = entryData.get("baDebitAmount");
                Object baCredit = entryData.get("baCreditAmount");
                
                voucher.setFaAccountCode((String) entryData.get("faAccountCode"));
                voucher.setFaAccountName((String) entryData.get("faAccountName"));
                voucher.setFaDebitAmount(faDebit != null ? new BigDecimal(faDebit.toString()) : BigDecimal.ZERO);
                voucher.setFaCreditAmount(faCredit != null ? new BigDecimal(faCredit.toString()) : BigDecimal.ZERO);
                voucher.setBaAccountCode((String) entryData.get("baAccountCode"));
                voucher.setBaAccountName((String) entryData.get("baAccountName"));
                voucher.setBaDebitAmount(baDebit != null ? new BigDecimal(baDebit.toString()) : BigDecimal.ZERO);
                voucher.setBaCreditAmount(baCredit != null ? new BigDecimal(baCredit.toString()) : BigDecimal.ZERO);
                
                voucher.setDebitAmount(voucher.getFaDebitAmount().add(voucher.getBaDebitAmount()));
                voucher.setCreditAmount(voucher.getFaCreditAmount().add(voucher.getBaCreditAmount()));
                
                voucherService.save(voucher);
            }
        }
        return Result.success();
    }

    @GetMapping("/year-summary")
    public Result<List<Map<String, Object>>> getYearSummary(
            @RequestParam String unitId,
            @RequestParam Integer year) {
        List<Map<String, Object>> summary = voucherService.getYearSummary(unitId, year);
        return Result.success(summary);
    }

    @GetMapping("/month-detail")
    public Result<Map<String, Object>> getMonthDetail(
            @RequestParam String unitId,
            @RequestParam Integer year,
            @RequestParam Integer month,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        List<Voucher> list = voucherService.getMonthDetail(unitId, year, month, pageNum, pageSize);
        Integer total = voucherService.getMonthTotalCount(unitId, year, month);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (total + pageSize - 1) / pageSize);
        
        return Result.success(result);
    }

    @GetMapping("/available-years")
    public Result<List<Integer>> getAvailableYears(@RequestParam String unitId) {
        List<Integer> years = voucherService.getAvailableYears(unitId);
        return Result.success(years);
    }

    @GetMapping("/{id}")
    public Result<Voucher> getById(@PathVariable String id) {
        Voucher voucher = voucherService.getById(id);
        return Result.success(voucher);
    }

    @GetMapping("/entries/{voucherNo}")
    public Result<List<Voucher>> getEntriesByVoucherNo(@PathVariable String voucherNo) {
        List<Voucher> entries = voucherService.getEntriesByVoucherNo(voucherNo);
        return Result.success(entries);
    }

    @PostMapping
    public Result<Void> save(@RequestBody Voucher voucher) {
        voucherService.save(voucher);
        return Result.success();
    }

    @PutMapping
    public Result<Void> update(@RequestBody Voucher voucher) {
        voucherService.updateById(voucher);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        voucherService.removeById(id);
        return Result.success();
    }
}