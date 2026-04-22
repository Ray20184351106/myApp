-- ==================== 修复部门表结构 ====================
-- 用于 mes_auth 库中的 sys_dept 表

USE mes_auth;

-- 删除旧表（如果存在）
DROP TABLE IF EXISTS `sys_dept`;

-- 创建部门表
CREATE TABLE `sys_dept` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门 ID',
    `parent_id` bigint(20) DEFAULT 0 COMMENT '父部门 ID',
    `dept_name` varchar(50) NOT NULL COMMENT '部门名称',
    `dept_code` varchar(50) NOT NULL COMMENT '部门编码',
    `sort` int(4) DEFAULT 0 COMMENT '显示顺序',
    `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
    `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 初始化部门数据
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `sort`, `leader`, `phone`, `status`) VALUES
(1, 0, '总公司', 'ROOT', 1, '管理员', '13800000000', 1),
(2, 1, '生产部', 'PROD', 1, '张三', '13800000001', 1),
(3, 1, '质量部', 'QC', 2, '李四', '13800000002', 1),
(4, 1, '设备部', 'EQUIP', 3, '王五', '13800000003', 1),
(5, 2, '生产一车间', 'PROD01', 1, '赵六', '13800000004', 1),
(6, 2, '生产二车间', 'PROD02', 2, '钱七', '13800000005', 1);

-- 添加部门管理菜单
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `visible`, `status`, `icon`) VALUES
(5, '部门管理', 1, 4, '/system/dept', 'system/Dept', 'C', 1, 1, 'tree')
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`);
