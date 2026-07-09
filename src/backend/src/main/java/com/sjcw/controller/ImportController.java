package com.sjcw.controller;

import com.sjcw.common.Result;
import com.sjcw.entity.ImportRecord;
import com.sjcw.entity.Voucher;
import com.sjcw.service.ImportRecordService;
import com.sjcw.service.VoucherService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private ImportRecordService importRecordService;

    @PostMapping("/voucher")
    public Result<Void> importVoucher(@RequestParam("file") MultipartFile file, @RequestParam("unitId") String unitId) {
        System.out.println("===== 开始导入凭证 =====");
        System.out.println("文件名: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize());
        System.out.println("单位ID: " + unitId);
        
        int totalCount = 0;
        int successCount = 0;
        int failCount = 0;
        String importStatus = "success";

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = null;
            String filename = file.getOriginalFilename();
            if (filename != null && filename.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (filename != null && filename.endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            }

            if (workbook == null) {
                System.out.println("错误：不支持的文件格式");
                return Result.error("不支持的文件格式");
            }

            int sheetCount = workbook.getNumberOfSheets();
            System.out.println("Sheet数量: " + sheetCount);
            
            Sheet sheet = null;
            for (int s = 0; s < sheetCount; s++) {
                Sheet sSheet = workbook.getSheetAt(s);
                int rowCount = sSheet.getLastRowNum();
                System.out.println("Sheet " + s + " 名称: " + sSheet.getSheetName() + ", 行数: " + rowCount);
                if (rowCount > 0 && sheet == null) {
                    sheet = sSheet;
                    System.out.println("选择Sheet " + s + " 作为数据来源");
                }
            }
            
            if (sheet == null) {
                System.out.println("错误：未找到包含数据的Sheet");
                return Result.error("未找到包含数据的Sheet");
            }
            
            int lastRowNum = sheet.getLastRowNum();
            System.out.println("总行数: " + lastRowNum);
            
            List<Voucher> voucherList = new ArrayList<>();
            
            for (int i = 3; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String voucherType = getCellStringValue(row.getCell(2));
                Integer entryNo = getCellIntValue(row.getCell(1));
                String voucherNo = voucherType + entryNo;
                
                if (entryNo == null) {
                    continue;
                }

                totalCount++;

                try {
                    Voucher voucher = new Voucher();
                    voucher.setUnitId(unitId);
                    voucher.setVoucherNo(voucherNo);
                    voucher.setVoucherDate(parseDate(getCellStringValue(row.getCell(0))));
                    voucher.setEntryNo(entryNo);
                    voucher.setSummary(getCellStringValue(row.getCell(4)));
                    voucher.setStatus("DRAFT");

                    String accountingSystem = getCellStringValue(row.getCell(3));
                    String accountCode = getCellStringValue(row.getCell(5));
                    String accountName = getCellStringValue(row.getCell(6));
                    BigDecimal debitAmount = getCellDecimalValue(row.getCell(7));
                    BigDecimal creditAmount = getCellDecimalValue(row.getCell(8));

                    if ("财务".equals(accountingSystem)) {
                        voucher.setFaAccountCode(accountCode);
                        voucher.setFaAccountName(accountName);
                        voucher.setFaDebitAmount(debitAmount);
                        voucher.setFaCreditAmount(creditAmount);
                        voucher.setBaAccountCode(null);
                        voucher.setBaAccountName(null);
                        voucher.setBaDebitAmount(BigDecimal.ZERO);
                        voucher.setBaCreditAmount(BigDecimal.ZERO);
                    } else if ("预算".equals(accountingSystem)) {
                        voucher.setBaAccountCode(accountCode);
                        voucher.setBaAccountName(accountName);
                        voucher.setBaDebitAmount(debitAmount);
                        voucher.setBaCreditAmount(creditAmount);
                        voucher.setFaAccountCode(null);
                        voucher.setFaAccountName(null);
                        voucher.setFaDebitAmount(BigDecimal.ZERO);
                        voucher.setFaCreditAmount(BigDecimal.ZERO);
                    }

                    BigDecimal debitTotal = voucher.getFaDebitAmount() != null ? voucher.getFaDebitAmount() : BigDecimal.ZERO;
                    debitTotal = debitTotal.add(voucher.getBaDebitAmount() != null ? voucher.getBaDebitAmount() : BigDecimal.ZERO);
                    BigDecimal creditTotal = voucher.getFaCreditAmount() != null ? voucher.getFaCreditAmount() : BigDecimal.ZERO;
                    creditTotal = creditTotal.add(voucher.getBaCreditAmount() != null ? voucher.getBaCreditAmount() : BigDecimal.ZERO);

                    voucher.setDebitAmount(debitTotal);
                    voucher.setCreditAmount(creditTotal);
                    
                    voucherList.add(voucher);
                    successCount++;

                    System.out.println("成功解析第" + i + "行数据: " + voucherNo + "_" + entryNo + ", 体系: " + accountingSystem + ", 借方: " + debitAmount + ", 贷方: " + creditAmount);
                } catch (Exception e) {
                    failCount++;
                    importStatus = "fail";
                    System.out.println("解析第" + i + "行失败: " + e.getMessage());
                }
            }

            List<Voucher> vouchers = voucherList;

            System.out.println("准备保存" + vouchers.size() + "条凭证数据");
            if (!vouchers.isEmpty()) {
                voucherService.saveBatch(vouchers);
                System.out.println("凭证数据保存成功");
            }

            ImportRecord record = new ImportRecord();
            record.setFileName(file.getOriginalFilename());
            record.setFilePath("/uploads/" + file.getOriginalFilename());
            record.setImportType("voucher");
            record.setTotalCount(totalCount);
            record.setSuccessCount(successCount);
            record.setFailCount(failCount);
            record.setStatus(importStatus);
            record.setImportedBy("00000000-0000-0000-0000-000000000001");
            importRecordService.save(record);
            System.out.println("导入记录保存成功");

            workbook.close();

        } catch (Exception e) {
            System.out.println("导入异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("导入失败：" + e.getMessage());
        }

        System.out.println("===== 导入完成 =====");
        return Result.success();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    private BigDecimal getCellDecimalValue(Cell cell) {
        if (cell == null) return BigDecimal.ZERO;
        switch (cell.getCellType()) {
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue());
            case STRING:
                try {
                    return new BigDecimal(cell.getStringCellValue().trim());
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            default:
                return BigDecimal.ZERO;
        }
    }

    private Integer getCellIntValue(Cell cell) {
        if (cell == null) return 0;
        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (Exception e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            return LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                return LocalDate.parse(dateStr, formatter);
            } catch (Exception ex) {
                return null;
            }
        }
    }
}