-- ==================== Nacos 配置数据库 ====================
CREATE DATABASE IF NOT EXISTS nacos_config DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Nacos 表结构 (简化版，完整表结构需要从 Nacos 官方获取)
USE nacos_config;

CREATE TABLE IF NOT EXISTS `config_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `data_id` varchar(255) NOT NULL,
    `group_id` varchar(128) DEFAULT NULL,
    `content` longtext NOT NULL,
    `md5` varchar(32) DEFAULT NULL,
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
    `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP,
    `src_user` text,
    `src_ip` varchar(50) DEFAULT NULL,
    `app_name` varchar(128) DEFAULT NULL,
    `tenant_id` varchar(128) DEFAULT '',
    `c_desc` varchar(256) DEFAULT NULL,
    `c_use` varchar(64) DEFAULT NULL,
    `effect` varchar(64) DEFAULT NULL,
    `type` varchar(64) DEFAULT NULL,
    `c_schema` text,
    `encrypted_data_key` text NOT NULL COMMENT '密钥',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
);

CREATE TABLE IF NOT EXISTS `tenant_info` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `kp` varchar(128) NOT NULL,
    `tenant_id` varchar(128) DEFAULT '',
    `tenant_name` varchar(128) DEFAULT '',
    `tenant_desc` varchar(256) DEFAULT NULL,
    `create_source` varchar(32) DEFAULT NULL,
    `gmt_create` bigint(20) NOT NULL,
    `gmt_modified` bigint(20) NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`)
);
