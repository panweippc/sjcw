USE master
GO

IF EXISTS (SELECT * FROM sys.databases WHERE name = 'sjcw')
BEGIN
    ALTER DATABASE sjcw SET SINGLE_USER WITH ROLLBACK IMMEDIATE
    DROP DATABASE sjcw
END
GO

CREATE DATABASE sjcw
ON PRIMARY (
    NAME = sjcw_data,
    FILENAME = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\DATA\sjcw.mdf',
    SIZE = 10MB,
    MAXSIZE = UNLIMITED,
    FILEGROWTH = 10MB
)
LOG ON (
    NAME = sjcw_log,
    FILENAME = 'C:\Program Files\Microsoft SQL Server\MSSQL17.SQLEXPRESS\MSSQL\DATA\sjcw.ldf',
    SIZE = 5MB,
    MAXSIZE = UNLIMITED,
    FILEGROWTH = 5MB
)
COLLATE Chinese_PRC_CI_AS
GO

USE sjcw
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

CREATE TABLE sjcw_db_version (
    version_id INT IDENTITY(1,1) PRIMARY KEY,
    version_number VARCHAR(20) NOT NULL,
    description VARCHAR(500) NULL,
    apply_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_version_number UNIQUE (version_number)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '数据库版本记录表', 'SCHEMA', 'dbo', 'TABLE', 'sjcw_db_version'
GO

CREATE TABLE sys_role (
    role_id VARCHAR(36) PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    is_enabled BIT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    updated_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_role_code UNIQUE (role_code),
    CONSTRAINT uk_role_name UNIQUE (role_name)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '角色表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role'
GO

CREATE TABLE sys_unit (
    unit_id VARCHAR(36) PRIMARY KEY,
    unit_name VARCHAR(200) NOT NULL,
    unit_code VARCHAR(100) NULL,
    parent_id VARCHAR(36) NULL,
    unit_level INT NOT NULL DEFAULT 1,
    unit_path VARCHAR(1000) NOT NULL DEFAULT '/',
    address VARCHAR(500) NULL,
    phone VARCHAR(20) NULL,
    contact_person VARCHAR(100) NULL,
    description VARCHAR(500) NULL,
    is_enabled BIT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    updated_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_unit_name UNIQUE (unit_name),
    CONSTRAINT fk_unit_parent FOREIGN KEY (parent_id) REFERENCES sys_unit(unit_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '单位表', 'SCHEMA', 'dbo', 'TABLE', 'sys_unit'
GO

CREATE TABLE sys_menu (
    menu_id VARCHAR(36) PRIMARY KEY,
    menu_name VARCHAR(100) NOT NULL,
    menu_code VARCHAR(50) NOT NULL,
    parent_id VARCHAR(36) NULL,
    menu_level INT NOT NULL DEFAULT 1,
    menu_path VARCHAR(500) NULL,
    icon VARCHAR(100) NULL,
    sort_order INT NOT NULL DEFAULT 0,
    is_enabled BIT NOT NULL DEFAULT 1,
    is_visible BIT NOT NULL DEFAULT 1,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    updated_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_menu_code UNIQUE (menu_code)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '菜单表', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu'
GO

CREATE TABLE sys_user (
    user_id VARCHAR(36) PRIMARY KEY,
    login_name VARCHAR(50) NOT NULL,
    password VARCHAR(200) NOT NULL,
    real_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NULL,
    email VARCHAR(100) NULL,
    unit_id VARCHAR(36) NULL,
    is_enabled BIT NOT NULL DEFAULT 1,
    last_login_time DATETIME NULL,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    updated_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_login_name UNIQUE (login_name),
    CONSTRAINT fk_user_unit FOREIGN KEY (unit_id) REFERENCES sys_unit(unit_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '用户表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user'
GO

CREATE TABLE sys_permission (
    permission_id VARCHAR(36) PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL,
    permission_code VARCHAR(50) NOT NULL,
    menu_id VARCHAR(36) NOT NULL,
    description VARCHAR(500) NULL,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_permission_code UNIQUE (permission_code),
    CONSTRAINT fk_permission_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(menu_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '权限表', 'SCHEMA', 'dbo', 'TABLE', 'sys_permission'
GO

CREATE TABLE sys_template (
    template_id VARCHAR(36) PRIMARY KEY,
    template_name VARCHAR(200) NOT NULL,
    template_code VARCHAR(50) NOT NULL,
    template_type VARCHAR(50) NULL,
    content TEXT NULL,
    description VARCHAR(500) NULL,
    is_enabled BIT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    updated_time DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT uk_template_code UNIQUE (template_code)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '模板表', 'SCHEMA', 'dbo', 'TABLE', 'sys_template'
GO

CREATE TABLE sys_role_menu (
    role_id VARCHAR(36) NOT NULL,
    menu_id VARCHAR(36) NOT NULL,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (role_id, menu_id),
    CONSTRAINT fk_role_menu_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id),
    CONSTRAINT fk_role_menu_menu FOREIGN KEY (menu_id) REFERENCES sys_menu(menu_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '角色菜单关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_menu'
GO

CREATE TABLE sys_role_permission (
    role_id VARCHAR(36) NOT NULL,
    permission_id VARCHAR(36) NOT NULL,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_role_permission_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id),
    CONSTRAINT fk_role_permission_permission FOREIGN KEY (permission_id) REFERENCES sys_permission(permission_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '角色权限关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_permission'
GO

CREATE TABLE sys_user_role (
    user_id VARCHAR(36) NOT NULL,
    role_id VARCHAR(36) NOT NULL,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES sys_user(user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES sys_role(role_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '用户角色关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_role'
GO

CREATE TABLE sys_import_record (
    import_id VARCHAR(36) PRIMARY KEY,
    file_name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    total_count INT NOT NULL DEFAULT 0,
    success_count INT NOT NULL DEFAULT 0,
    fail_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'processing',
    error_message TEXT NULL,
    imported_by VARCHAR(36) NOT NULL,
    created_time DATETIME NOT NULL DEFAULT GETDATE(),
    completed_time DATETIME NULL,
    CONSTRAINT fk_import_record_user FOREIGN KEY (imported_by) REFERENCES sys_user(user_id)
)
GO
EXEC sp_addextendedproperty 'MS_Description', '批量导入记录表', 'SCHEMA', 'dbo', 'TABLE', 'sys_import_record'
GO

INSERT INTO sjcw_db_version (version_number, description) VALUES ('1.0.0', '基础管理模块初始化：角色管理、用户管理、单位管理、模板管理、批量导入')
GO

INSERT INTO sys_role (role_id, role_name, role_code, description, is_enabled, sort_order) VALUES ('00000000-0000-0000-0000-000000000001', '超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限', 1, 1)
GO
INSERT INTO sys_role (role_id, role_name, role_code, description, is_enabled, sort_order) VALUES ('00000000-0000-0000-0000-000000000002', '审计管理员', 'AUDIT_ADMIN', '审计管理员，负责审计相关业务', 1, 2)
GO
INSERT INTO sys_role (role_id, role_name, role_code, description, is_enabled, sort_order) VALUES ('00000000-0000-0000-0000-000000000003', '普通用户', 'NORMAL_USER', '普通用户，拥有基础查询权限', 1, 3)
GO

INSERT INTO sys_unit (unit_id, unit_name, unit_code, parent_id, unit_level, unit_path, is_enabled, sort_order) VALUES ('00000000-0000-0000-0000-000000000001', '系统根单位', 'ROOT', NULL, 1, '/', 1, 1)
GO

INSERT INTO sys_user (user_id, login_name, password, real_name, unit_id, is_enabled) VALUES ('00000000-0000-0000-0000-000000000001', 'admin', 'E10ADC3949BA59ABBE56E057F20F883E', '系统管理员', '00000000-0000-0000-0000-000000000001', 1)
GO

INSERT INTO sys_menu (menu_id, menu_name, menu_code, parent_id, menu_level, menu_path, icon, sort_order, is_enabled, is_visible) VALUES ('00000000-0000-0000-0000-000000000001', '基础管理', 'BASE_MANAGE', NULL, 1, '/base', 'icon-settings', 1, 1, 1)
GO
INSERT INTO sys_menu (menu_id, menu_name, menu_code, parent_id, menu_level, menu_path, icon, sort_order, is_enabled, is_visible) VALUES ('00000000-0000-0000-0000-000000000002', '角色管理', 'ROLE_MANAGE', '00000000-0000-0000-0000-000000000001', 2, '/base/role', 'icon-role', 1, 1, 1)
GO
INSERT INTO sys_menu (menu_id, menu_name, menu_code, parent_id, menu_level, menu_path, icon, sort_order, is_enabled, is_visible) VALUES ('00000000-0000-0000-0000-000000000003', '用户管理', 'USER_MANAGE', '00000000-0000-0000-0000-000000000001', 2, '/base/user', 'icon-user', 2, 1, 1)
GO
INSERT INTO sys_menu (menu_id, menu_name, menu_code, parent_id, menu_level, menu_path, icon, sort_order, is_enabled, is_visible) VALUES ('00000000-0000-0000-0000-000000000004', '单位管理', 'UNIT_MANAGE', '00000000-0000-0000-0000-000000000001', 2, '/base/unit', 'icon-organization', 3, 1, 1)
GO
INSERT INTO sys_menu (menu_id, menu_name, menu_code, parent_id, menu_level, menu_path, icon, sort_order, is_enabled, is_visible) VALUES ('00000000-0000-0000-0000-000000000005', '模板管理', 'TEMPLATE_MANAGE', '00000000-0000-0000-0000-000000000001', 2, '/base/template', 'icon-template', 4, 1, 1)
GO
INSERT INTO sys_menu (menu_id, menu_name, menu_code, parent_id, menu_level, menu_path, icon, sort_order, is_enabled, is_visible) VALUES ('00000000-0000-0000-0000-000000000006', '批量导入', 'BATCH_IMPORT', '00000000-0000-0000-0000-000000000001', 2, '/base/import', 'icon-upload', 5, 1, 1)
GO

INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000001', '角色查询', 'ROLE_QUERY', '00000000-0000-0000-0000-000000000002', '查看角色列表')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000002', '角色新增', 'ROLE_ADD', '00000000-0000-0000-0000-000000000002', '新增角色')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000003', '角色编辑', 'ROLE_EDIT', '00000000-0000-0000-0000-000000000002', '编辑角色信息')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000004', '角色删除', 'ROLE_DELETE', '00000000-0000-0000-0000-000000000002', '删除角色')
GO

INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000005', '用户查询', 'USER_QUERY', '00000000-0000-0000-0000-000000000003', '查看用户列表')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000006', '用户新增', 'USER_ADD', '00000000-0000-0000-0000-000000000003', '新增用户')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000007', '用户编辑', 'USER_EDIT', '00000000-0000-0000-0000-000000000003', '编辑用户信息')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000008', '用户删除', 'USER_DELETE', '00000000-0000-0000-0000-000000000003', '删除用户')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000009', '用户重置密码', 'USER_RESET_PWD', '00000000-0000-0000-0000-000000000003', '重置用户密码')
GO

INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000010', '单位查询', 'UNIT_QUERY', '00000000-0000-0000-0000-000000000004', '查看单位列表')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000011', '单位新增', 'UNIT_ADD', '00000000-0000-0000-0000-000000000004', '新增单位')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000012', '单位编辑', 'UNIT_EDIT', '00000000-0000-0000-0000-000000000004', '编辑单位信息')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000013', '单位删除', 'UNIT_DELETE', '00000000-0000-0000-0000-000000000004', '删除单位')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000014', '单位添加下级', 'UNIT_ADD_CHILD', '00000000-0000-0000-0000-000000000004', '添加下级单位')
GO

INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000015', '模板查询', 'TEMPLATE_QUERY', '00000000-0000-0000-0000-000000000005', '查看模板列表')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000016', '模板新增', 'TEMPLATE_ADD', '00000000-0000-0000-0000-000000000005', '新增模板')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000017', '模板编辑', 'TEMPLATE_EDIT', '00000000-0000-0000-0000-000000000005', '编辑模板')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000018', '模板删除', 'TEMPLATE_DELETE', '00000000-0000-0000-0000-000000000005', '删除模板')
GO

INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000019', '批量导入', 'BATCH_IMPORT_EXEC', '00000000-0000-0000-0000-000000000006', '执行批量导入')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000020', '导入记录查询', 'IMPORT_RECORD_QUERY', '00000000-0000-0000-0000-000000000006', '查看导入记录')
GO
INSERT INTO sys_permission (permission_id, permission_name, permission_code, menu_id, description) VALUES ('00000000-0000-0000-0000-000000000021', '导入记录删除', 'IMPORT_RECORD_DELETE', '00000000-0000-0000-0000-000000000006', '删除导入记录')
GO

INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006')
GO

INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000002')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000004')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000005')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000006')
GO

INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000002')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000003')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000004')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000005')
GO
INSERT INTO sys_role_menu (role_id, menu_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000006')
GO

INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000002')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000003')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000004')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000005')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000006')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000007')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000008')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000009')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000010')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000011')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000012')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000013')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000014')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000015')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000016')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000017')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000018')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000019')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000020')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000021')
GO

INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000003')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000005')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000007')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000010')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000011')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000012')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000014')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000015')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000017')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000019')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000020')
GO

INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000005')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000010')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000015')
GO
INSERT INTO sys_role_permission (role_id, permission_id) VALUES ('00000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000020')
GO

INSERT INTO sys_user_role (user_id, role_id) VALUES ('00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001')
GO

CREATE INDEX idx_unit_parent ON sys_unit(parent_id)
GO
CREATE INDEX idx_unit_path ON sys_unit(unit_path)
GO
CREATE INDEX idx_user_unit ON sys_user(unit_id)
GO
CREATE INDEX idx_role_menu_role ON sys_role_menu(role_id)
GO
CREATE INDEX idx_role_menu_menu ON sys_role_menu(menu_id)
GO
CREATE INDEX idx_role_permission_role ON sys_role_permission(role_id)
GO
CREATE INDEX idx_role_permission_permission ON sys_role_permission(permission_id)
GO
CREATE INDEX idx_permission_menu ON sys_permission(menu_id)
GO
CREATE INDEX idx_user_role_user ON sys_user_role(user_id)
GO
CREATE INDEX idx_user_role_role ON sys_user_role(role_id)
GO
CREATE INDEX idx_import_record_user ON sys_import_record(imported_by)
GO

PRINT '数据库初始化完成！'
GO