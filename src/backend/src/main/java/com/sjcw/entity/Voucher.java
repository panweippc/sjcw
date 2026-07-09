package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_voucher")
public class Voucher {

    @TableId(value = "voucher_id", type = IdType.ASSIGN_UUID)
    private String voucherId;

    @TableField("unit_id")
    private String unitId;

    @TableField("voucher_no")
    private String voucherNo;

    @TableField("voucher_date")
    private LocalDate voucherDate;

    @TableField("summary")
    private String summary;

    @TableField("debit_amount")
    private BigDecimal debitAmount;

    @TableField("credit_amount")
    private BigDecimal creditAmount;

    @TableField("creator")
    private String creator;

    @TableField("auditor")
    private String auditor;

    @TableField("bookkeeper")
    private String bookkeeper;

    @TableField("cashier")
    private String cashier;

    @TableField("status")
    private String status;

    @TableField("entry_no")
    private Integer entryNo;

    @TableField("fa_account_code")
    private String faAccountCode;

    @TableField("fa_account_name")
    private String faAccountName;

    @TableField("fa_debit_amount")
    private BigDecimal faDebitAmount;

    @TableField("fa_credit_amount")
    private BigDecimal faCreditAmount;

    @TableField("ba_account_code")
    private String baAccountCode;

    @TableField("ba_account_name")
    private String baAccountName;

    @TableField("ba_debit_amount")
    private BigDecimal baDebitAmount;

    @TableField("ba_credit_amount")
    private BigDecimal baCreditAmount;

    @TableField("created_time")
    private LocalDateTime createdTime;

    @TableField("updated_time")
    private LocalDateTime updatedTime;

    @TableField(exist = false)
    private String unitName;
}