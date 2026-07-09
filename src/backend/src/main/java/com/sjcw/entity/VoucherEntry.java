package com.sjcw.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sys_voucher_entry")
public class VoucherEntry {

    @TableId(value = "entry_id", type = IdType.ASSIGN_UUID)
    private String entryId;

    @TableField("voucher_id")
    private String voucherId;

    @TableField("entry_no")
    private Integer entryNo;

    @TableField("account_code")
    private String accountCode;

    @TableField("account_name")
    private String accountName;

    @TableField("summary")
    private String summary;

    @TableField("debit_amount")
    private BigDecimal debitAmount;

    @TableField("credit_amount")
    private BigDecimal creditAmount;

    @TableField("created_time")
    private LocalDateTime createdTime;
}