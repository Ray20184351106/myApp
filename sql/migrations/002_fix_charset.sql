-- ==================== 修复 mes_auth 数据库乱码问题 ====================
-- 执行此脚本前请确保：
-- 1. MySQL 服务正在运行
-- 2. 有足够的权限执行 ALTER DATABASE 和修改表操作

-- 步骤 1: 检查当前数据库字符集
SHOW CREATE DATABASE mes_auth;

-- 步骤 2: 修改数据库默认字符集
ALTER DATABASE mes_auth CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 步骤 3: 修改所有表的字符集
-- 用户表
ALTER TABLE mes_auth.sys_user CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 角色表
ALTER TABLE mes_auth.sys_role CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户角色关联表
ALTER TABLE mes_auth.sys_user_role CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 菜单表
ALTER TABLE mes_auth.sys_menu CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 部门表
ALTER TABLE mes_auth.sys_dept CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 角色菜单关联表
ALTER TABLE mes_auth.sys_role_menu CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 步骤 4: 验证表字符集
SELECT
    TABLE_NAME,
    TABLE_COLLATION
FROM
    information_schema.TABLES
WHERE
    TABLE_SCHEMA = 'mes_auth';

-- 步骤 5: 如果数据已经乱码，需要重新插入正确数据
-- 先备份现有数据（可选）
-- CREATE TABLE mes_auth.sys_user_backup AS SELECT * FROM mes_auth.sys_user;

-- 清空并重新插入用户数据（如果数据已乱码）
-- TRUNCATE TABLE mes_auth.sys_user;
-- INSERT INTO mes_auth.sys_user (id, username, password, nickname, status) VALUES
-- (1, 'admin', '$2a$10$LqI04K7rzp5vm4XO5afeN.x.37K0UjwkNJvpMiC2IDxbpBOidfLwa', '系统管理员', 1),
-- (2, 'operator', '$2a$10$LqI04K7rzp5vm4XO5afeN.x.37K0UjwkNJvpMiC2IDxbpBOidfLwa', '操作员', 1);

-- 清空并重新插入角色数据（如果数据已乱码）
-- TRUNCATE TABLE mes_auth.sys_role;
-- INSERT INTO mes_auth.sys_role (id, role_name, role_key, sort, status) VALUES
-- (1, '超级管理员', 'admin', 1, 1),
-- (2, '生产管理员', 'production', 2, 1),
-- (3, '质量管理员', 'quality', 3, 1),
-- (4, '操作员', 'operator', 4, 1);

-- 清空并重新插入部门数据（如果数据已乱码）
-- TRUNCATE TABLE mes_auth.sys_dept;
-- INSERT INTO mes_auth.sys_dept (id, parent_id, dept_name, dept_code, sort, leader, phone, status) VALUES
-- (1, 0, '总公司', 'ROOT', 1, '管理员', '13800000000', 1),
-- (2, 1, '生产部', 'PROD', 1, '张三', '13800000001', 1),
-- (3, 1, '质量部', 'QC', 2, '李四', '13800000002', 1),
-- (4, 1, '设备部', 'EQUIP', 3, '王五', '13800000003', 1),
-- (5, 2, '生产一车间', 'PROD01', 1, '赵六', '13800000004', 1),
-- (6, 2, '生产二车间', 'PROD02', 2, '钱七', '13800000005', 1);

-- 清空并重新插入菜单数据（如果数据已乱码）
-- TRUNCATE TABLE mes_auth.sys_menu;
-- INSERT INTO mes_auth.sys_menu (id, menu_name, parent_id, sort, path, component, menu_type, visible, status, icon) VALUES
-- (1, '系统管理', 0, 1, '/system', NULL, 'M', 1, 1, 'setting'),
-- (2, '用户管理', 1, 1, '/system/user', 'system/user/index', 'C', 1, 1, 'user'),
-- (3, '角色管理', 1, 2, '/system/role', 'system/role/index', 'C', 1, 1, 'peoples'),
-- (4, '菜单管理', 1, 3, '/system/menu', 'system/menu/index', 'C', 1, 1, 'tree-table'),
-- (5, '部门管理', 1, 4, '/system/dept', 'system/dept/index', 'C', 1, 1, 'tree'),
-- (100, '生产管理', 0, 2, '/production', NULL, 'M', 1, 1, 'build'),
-- (101, '工单管理', 100, 1, '/production/order', 'production/order/index', 'C', 1, 1, 'list'),
-- (102, '工艺路线', 100, 2, '/production/process', 'production/process/index', 'C', 1, 1, 'tree'),
-- (200, '质量管理', 0, 3, '/quality', NULL, 'M', 1, 1, 'checkbox'),
-- (201, '检验标准', 200, 1, '/quality/standard', 'quality/standard/index', 'C', 1, 1, 'documentation'),
-- (300, '设备管理', 0, 4, '/equipment', NULL, 'M', 1, 1, 'monitor'),
-- (400, '数据采集', 0, 5, '/datacollect', NULL, 'M', 1, 1, 'chart');

-- 步骤 6: 同样修复 mes_system 数据库
ALTER DATABASE mes_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 注意: sys_dept 表已在 mes_auth 库中维护，mes_system 库中不应存在
-- ALTER TABLE mes_system.sys_dept CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mes_system.sys_dict_type CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mes_system.sys_dict_data CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mes_system.sys_config CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE mes_system.sys_oper_log CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 完成
SELECT '字符集修复完成!' AS message;