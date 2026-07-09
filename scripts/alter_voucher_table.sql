ALTER TABLE sys_voucher
ADD auditor VARCHAR(100) NULL,
    bookkeeper VARCHAR(100) NULL,
    cashier VARCHAR(100) NULL,
    entry_no INT NOT NULL DEFAULT 1,
    fa_account_code VARCHAR(50) NULL,
    fa_account_name VARCHAR(200) NULL,
    fa_debit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    fa_credit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    ba_account_code VARCHAR(50) NULL,
    ba_account_name VARCHAR(200) NULL,
    ba_debit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    ba_credit_amount DECIMAL(18,2) NOT NULL DEFAULT 0;