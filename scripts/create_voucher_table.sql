CREATE TABLE sys_voucher (
    voucher_id VARCHAR(36) PRIMARY KEY,
    unit_id VARCHAR(36) NOT NULL,
    voucher_no VARCHAR(50) NOT NULL,
    voucher_date DATE NOT NULL,
    summary VARCHAR(500) NULL,
    debit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    credit_amount DECIMAL(18,2) NOT NULL DEFAULT 0,
    creator VARCHAR(100) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    updated_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT fk_voucher_unit FOREIGN KEY (unit_id) REFERENCES sys_unit(unit_id)
)