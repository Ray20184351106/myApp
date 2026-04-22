# 项目准则

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
