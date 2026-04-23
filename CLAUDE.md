# 项目准则

## 项目分析

分析本项目时，优先读取 README.md 文件了解项目当前状态，包括：
- 项目架构和技术栈
- 开发进度和已完成功能
- 数据库设计和表结构
- API 接口文档
- 最近更新记录

## 文档同步

每次修改项目代码或配置后，必须同步更新 README.md 文件，确保以下内容是最新的：
- 开发进度（已完成/待开发功能）
- 项目结构（新增/删除的文件）
- API 接口（新增/修改的接口）
- 配置说明（新增/修改的配置）
- 最近更新记录（日期和变更内容）

## 数据库操作

所有数据库操作必须通过命令行执行，使用以下连接方式：

```bash
mysql -u root -pmysql --default-character-set=utf8mb4 <database_name>
```

### 常用操作示例

```bash
# 查看数据库列表
mysql -u root -pmysql -e "SHOW DATABASES;"

# 查看表结构
mysql -u root -pmysql --default-character-set=utf8mb4 <database_name> -e "SHOW CREATE TABLE <table_name>\G"

# 执行 SQL 文件
mysql -u root -pmysql --default-character-set=utf8mb4 <database_name> < /path/to/script.sql

# 执行内联 SQL
mysql -u root -pmysql --default-character-set=utf8mb4 <database_name> << 'EOF'
-- SQL 语句
EOF
```

### 注意事项

- 始终使用 `--default-character-set=utf8mb4` 参数以避免中文乱码
- 密码为 `mysql`，用户名为 `root`
- 修改表结构时使用 `ALTER TABLE` 语句
