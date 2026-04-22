-- ==================== MES 认证数据库 ====================
CREATE DATABASE IF NOT EXISTS mes_auth DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mes_auth;

-- 用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志 0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` varchar(50) NOT NULL COMMENT '角色名称',
    `role_key` varchar(50) NOT NULL COMMENT '角色权限字符',
    `sort` int(4) DEFAULT 0 COMMENT '显示顺序',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(1) DEFAULT 0 COMMENT '删除标志',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_key` (`role_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `user_id` bigint(20) NOT NULL COMMENT '用户ID',
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 菜单表
CREATE TABLE IF NOT EXISTS `sys_menu` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
    `parent_id` bigint(20) DEFAULT 0 COMMENT '父菜单ID',
    `sort` int(4) DEFAULT 0 COMMENT '显示顺序',
    `path` varchar(200) DEFAULT NULL COMMENT '路由地址',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `menu_type` char(1) DEFAULT '' COMMENT '菜单类型 M-目录 C-菜单 F-按钮',
    `visible` tinyint(1) DEFAULT 1 COMMENT '是否显示 0-隐藏 1-显示',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
    `icon` varchar(100) DEFAULT NULL COMMENT '菜单图标',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 部门表
CREATE TABLE IF NOT EXISTS `sys_dept` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` bigint(20) DEFAULT 0 COMMENT '父部门ID',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 初始化数据
-- 默认密码: 123456 (BCrypt加密)
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `status`) VALUES
(1, 'admin', '$2a$10$LqI04K7rzp5vm4XO5afeN.x.37K0UjwkNJvpMiC2IDxbpBOidfLwa', '系统管理员', 1),
(2, 'operator', '$2a$10$LqI04K7rzp5vm4XO5afeN.x.37K0UjwkNJvpMiC2IDxbpBOidfLwa', '操作员', 1);

INSERT INTO `sys_role` (`id`, `role_name`, `role_key`, `sort`, `status`) VALUES
(1, '超级管理员', 'admin', 1, 1),
(2, '生产管理员', 'production', 2, 1),
(3, '质量管理员', 'quality', 3, 1),
(4, '操作员', 'operator', 4, 1);

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES
(1, 1),
(2, 4);

-- 部门初始化数据
INSERT INTO `sys_dept` (`id`, `parent_id`, `dept_name`, `dept_code`, `sort`, `leader`, `phone`, `status`) VALUES
(1, 0, '总公司', 'ROOT', 1, '管理员', '13800000000', 1),
(2, 1, '生产部', 'PROD', 1, '张三', '13800000001', 1),
(3, 1, '质量部', 'QC', 2, '李四', '13800000002', 1),
(4, 1, '设备部', 'EQUIP', 3, '王五', '13800000003', 1),
(5, 2, '生产一车间', 'PROD01', 1, '赵六', '13800000004', 1),
(6, 2, '生产二车间', 'PROD02', 2, '钱七', '13800000005', 1);

-- 菜单初始化
INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `visible`, `status`, `icon`) VALUES
(1, '系统管理', 0, 1, '/system', NULL, 'M', 1, 1, 'setting'),
(2, '用户管理', 1, 1, '/system/user', 'system/user/index', 'C', 1, 1, 'user'),
(3, '角色管理', 1, 2, '/system/role', 'system/role/index', 'C', 1, 1, 'peoples'),
(4, '菜单管理', 1, 3, '/system/menu', 'system/menu/index', 'C', 1, 1, 'tree-table'),
(5, '部门管理', 1, 4, '/system/dept', 'system/dept/index', 'C', 1, 1, 'tree'),
(100, '生产管理', 0, 2, '/production', NULL, 'M', 1, 1, 'build'),
(101, '工单管理', 100, 1, '/production/order', 'production/order/index', 'C', 1, 1, 'list'),
(102, '工艺路线', 100, 2, '/production/process', 'production/process/index', 'C', 1, 1, 'tree'),
(200, '质量管理', 0, 3, '/quality', NULL, 'M', 1, 1, 'checkbox'),
(201, '检验标准', 200, 1, '/quality/standard', 'quality/standard/index', 'C', 1, 1, 'documentation'),
(300, '设备管理', 0, 4, '/equipment', NULL, 'M', 1, 1, 'monitor'),
(400, '数据采集', 0, 5, '/datacollect', NULL, 'M', 1, 1, 'chart');
