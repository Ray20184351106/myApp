-- ==================== MES 本地开发数据库初始化 ====================
-- 在本地 MySQL 执行此脚本

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

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS `sys_role_menu` (
    `role_id` bigint(20) NOT NULL COMMENT '角色ID',
    `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 初始化数据 - 默认密码: 123456 (BCrypt加密)
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

INSERT INTO `sys_menu` (`id`, `menu_name`, `parent_id`, `sort`, `path`, `component`, `menu_type`, `visible`, `status`, `icon`) VALUES
(1, '系统管理', 0, 1, '/system', NULL, 'M', 1, 1, 'setting'),
(2, '用户管理', 1, 1, '/system/user', 'system/user/index', 'C', 1, 1, 'user'),
(3, '角色管理', 1, 2, '/system/role', 'system/role/index', 'C', 1, 1, 'peoples'),
(4, '菜单管理', 1, 3, '/system/menu', 'system/menu/index', 'C', 1, 1, 'tree-table'),
(100, '生产管理', 0, 2, '/production', NULL, 'M', 1, 1, 'build'),
(101, '工单管理', 100, 1, '/production/order', 'production/order/index', 'C', 1, 1, 'list'),
(102, '工艺路线', 100, 2, '/production/process', 'production/process/index', 'C', 1, 1, 'tree'),
(200, '质量管理', 0, 3, '/quality', NULL, 'M', 1, 1, 'checkbox'),
(201, '检验标准', 200, 1, '/quality/standard', 'quality/standard/index', 'C', 1, 1, 'documentation'),
(300, '设备管理', 0, 4, '/equipment', NULL, 'M', 1, 1, 'monitor'),
(400, '数据采集', 0, 5, '/datacollect', NULL, 'M', 1, 1, 'chart');

-- ==================== MES 系统数据库 ====================
CREATE DATABASE IF NOT EXISTS mes_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mes_system;

-- 注意: 部门表(sys_dept)已在 mes_auth 库中定义，此处不再重复定义

-- 字典类型表
CREATE TABLE IF NOT EXISTS `sys_dict_type` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name` varchar(100) NOT NULL COMMENT '字典名称',
    `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典数据表
CREATE TABLE IF NOT EXISTS `sys_dict_data` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort` int(4) DEFAULT 0 COMMENT '字典排序',
    `dict_label` varchar(100) NOT NULL COMMENT '字典标签',
    `dict_value` varchar(100) NOT NULL COMMENT '字典键值',
    `dict_type` varchar(100) NOT NULL COMMENT '字典类型',
    `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性',
    `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
    `is_default` tinyint(1) DEFAULT 0 COMMENT '是否默认 0-否 1-是',
    `status` tinyint(1) DEFAULT 1 COMMENT '状态 0-禁用 1-正常',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `sys_config` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    `config_name` varchar(100) NOT NULL COMMENT '参数名称',
    `config_key` varchar(100) NOT NULL COMMENT '参数键名',
    `config_value` varchar(500) NOT NULL COMMENT '参数键值',
    `config_type` tinyint(1) DEFAULT 1 COMMENT '系统内置 0-否 1-是',
    `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `sys_oper_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `title` varchar(50) DEFAULT NULL COMMENT '模块标题',
    `business_type` int(2) DEFAULT 0 COMMENT '业务类型',
    `method` varchar(200) DEFAULT NULL COMMENT '方法名称',
    `request_method` varchar(10) DEFAULT NULL COMMENT '请求方式',
    `oper_name` varchar(50) DEFAULT NULL COMMENT '操作人员',
    `dept_name` varchar(50) DEFAULT NULL COMMENT '部门名称',
    `oper_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
    `oper_ip` varchar(128) DEFAULT NULL COMMENT '主机地址',
    `oper_param` longtext COMMENT '请求参数',
    `json_result` longtext COMMENT '返回参数',
    `status` tinyint(1) DEFAULT 0 COMMENT '操作状态 0-正常 1-异常',
    `error_msg` varchar(2000) DEFAULT NULL COMMENT '错误消息',
    `oper_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    `cost_time` bigint(20) DEFAULT 0 COMMENT '消耗时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 初始化字典类型
INSERT INTO `sys_dict_type` (`id`, `dict_name`, `dict_type`, `status`) VALUES
(1, '用户性别', 'sys_user_sex', 1),
(2, '菜单状态', 'sys_show_hide', 1),
(3, '系统开关', 'sys_normal_disable', 1),
(4, '任务状态', 'sys_job_status', 1),
(5, '任务分组', 'sys_job_group', 1),
(6, '通知类型', 'sys_notice_type', 1),
(7, '工单状态', 'mes_order_status', 1),
(8, '设备状态', 'mes_equipment_status', 1),
(9, '检验结果', 'mes_check_result', 1);

-- 初始化字典数据
INSERT INTO `sys_dict_data` (`dict_sort`, `dict_label`, `dict_value`, `dict_type`, `status`) VALUES
(1, '男', '0', 'sys_user_sex', 1),
(2, '女', '1', 'sys_user_sex', 1),
(1, '显示', '0', 'sys_show_hide', 1),
(2, '隐藏', '1', 'sys_show_hide', 1),
(1, '正常', '0', 'sys_normal_disable', 1),
(2, '停用', '1', 'sys_normal_disable', 1),
(1, '待生产', '0', 'mes_order_status', 1),
(2, '生产中', '1', 'mes_order_status', 1),
(3, '已完成', '2', 'mes_order_status', 1),
(4, '已取消', '3', 'mes_order_status', 1),
(1, '运行', '0', 'mes_equipment_status', 1),
(2, '停机', '1', 'mes_equipment_status', 1),
(3, '故障', '2', 'mes_equipment_status', 1),
(1, '合格', '0', 'mes_check_result', 1),
(2, '不合格', '1', 'mes_check_result', 1);

-- 初始化系统配置
INSERT INTO `sys_config` (`id`, `config_name`, `config_key`, `config_value`, `config_type`) VALUES
(1, '用户初始密码', 'sys.user.initPassword', '123456', 1),
(2, '验证码开关', 'sys.account.captchaEnabled', 'true', 1),
(3, '账号自助注册', 'sys.account.registerUser', 'false', 1);
