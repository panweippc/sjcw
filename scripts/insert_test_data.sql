SET NOCOUNT ON;

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v1', '14e3f4e3b2c2d1952e93d3b46382140e', '记-001', '2026-01-05', '财政发放工资补贴', 165000.00, 165000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 1, '220101', '应付职工薪酬-基本工资', 165000.00, 0.00, '72010101', '事业支出-基本支出-人员经费', 165000.00, 0.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v2', '14e3f4e3b2c2d1952e93d3b46382140e', '记-001', '2026-01-05', '财政发放工资补贴', 165000.00, 165000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 2, '4001', '财政拨款收入', 0.00, 165000.00, '600101', '财政拨款预算收入', 0.00, 165000.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v3', '14e3f4e3b2c2d1952e93d3b46382140e', '记-002', '2026-01-10', '购买办公设备', 5000.00, 5000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 1, '1601', '固定资产', 5000.00, 0.00, '72010201', '事业支出-基本支出-办公费', 5000.00, 0.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v4', '14e3f4e3b2c2d1952e93d3b46382140e', '记-002', '2026-01-10', '购买办公设备', 5000.00, 5000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 2, '1002', '银行存款', 0.00, 5000.00, '', '', 0.00, 0.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v5', '14e3f4e3b2c2d1952e93d3b46382140e', '记-003', '2026-02-15', '报销差旅费', 2000.00, 2000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 1, '5001', '业务活动费用', 2000.00, 0.00, '72010202', '事业支出-基本支出-差旅费', 2000.00, 0.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v6', '14e3f4e3b2c2d1952e93d3b46382140e', '记-003', '2026-02-15', '报销差旅费', 2000.00, 2000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 2, '1002', '银行存款', 0.00, 2000.00, '', '', 0.00, 0.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v7', '14e3f4e3b2c2d1952e93d3b46382140e', '记-004', '2026-03-20', '计提折旧', 1000.00, 1000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 1, '5001', '业务活动费用', 1000.00, 0.00, '', '', 0.00, 0.00, GETDATE(), GETDATE());

INSERT INTO sys_voucher (voucher_id, unit_id, voucher_no, voucher_date, summary, debit_amount, credit_amount, creator, auditor, bookkeeper, cashier, status, entry_no, fa_account_code, fa_account_name, fa_debit_amount, fa_credit_amount, ba_account_code, ba_account_name, ba_debit_amount, ba_credit_amount, created_time, updated_time)
VALUES ('v8', '14e3f4e3b2c2d1952e93d3b46382140e', '记-004', '2026-03-20', '计提折旧', 1000.00, 1000.00, '张三', '李四', '王五', '赵六', 'DRAFT', 2, '1602', '固定资产累计折旧', 0.00, 1000.00, '', '', 0.00, 0.00, GETDATE(), GETDATE());

SELECT COUNT(*) AS total_rows FROM sys_voucher;