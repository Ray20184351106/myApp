-- ==================== 删除 mes_system 库中的冗余部门表 ====================
-- 部门表(sys_dept)应只在 mes_auth 库中维护
-- 执行此脚本前请确认 mes_system.sys_dept 中的数据已迁移或不再需要

USE mes_system;

-- 检查表是否存在并删除
DROP TABLE IF EXISTS `sys_dept`;

-- 执行成功提示
SELECT 'mes_system.sys_dept 表已删除，部门表统一在 mes_auth 库中维护' AS message;
