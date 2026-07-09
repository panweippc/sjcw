CREATE TABLE sys_voucher_entry (
    entry_id VARCHAR(36) PRIMARY KEY,
    voucher_id VARCHAR(36) NOT NULL,
    entry_no INT NOT NULL DEFAULT 1,
    account_code VARCHAR(50) NULL,
    account_name VARCHAR(200) NULL,
    summary VARCHAR(500) NULL,
    debit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    credit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_entry_voucher FOREIGN KEY (voucher_id) REFERENCES sys_voucher(voucher_id) ON DELETE CASCADE
)