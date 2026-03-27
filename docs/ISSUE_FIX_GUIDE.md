# MES 项目问题修复文档

> 文档生成时间：2026-03-26

---

## 一、环境配置问题

### 1.1 多环境配置缺失

**问题描述：**
- 项目只有单一配置文件，无法区分本地开发和 Docker 环境
- Docker MySQL 连接地址与本地不同

**解决方案：**
创建多环境配置文件：

```
mes-services/mes-auth/src/main/resources/
├── application.yml          # 通用配置
├── application-local.yml    # 本地开发环境
└── application-docker.yml   # Docker 环境
```

**application-local.yml 示例：**
```yaml
# 本地开发环境配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mes_auth?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: mysql
  data:
    redis:
      host: localhost
```

**IDEA 启动配置：**
- Active profiles: `local`

---

## 二、数据库问题

### 2.1 MySQL 连接认证失败

**问题描述：**
```
Access denied for user 'root'@'localhost' (using password: YES)
```

**原因分析：**
1. MySQL 8.0 默认使用 `caching_sha2_password` 认证插件
2. Docker MySQL 的 `localhost` 连接有特殊处理

**解决方案：**
```sql
-- 修改用户认证方式
ALTER USER 'root'@'%' IDENTIFIED WITH mysql_native_password BY 'root123';
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'root123';
FLUSH PRIVILEGES;
```

### 2.2 用户表跨库问题

**问题描述：**
- mes_system 服务查询用户时报错
- `mes_system` 数据库没有 `sys_user` 表

**解决方案：**
```sql
-- 在 mes_system 数据库创建 sys_user 表
USE mes_system;
CREATE TABLE IF NOT EXISTS sys_user (
    id bigint(20) NOT NULL AUTO_INCREMENT,
    username varchar(50) NOT NULL,
    password varchar(100) NOT NULL,
    nickname varchar(50) DEFAULT NULL,
    -- ... 其他字段
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 同步用户数据
INSERT INTO sys_user SELECT * FROM mes_auth.sys_user;
```

---

## 三、后端问题

### 3.1 Redis 序列化配置缺失

**问题描述：**
- 存储对象到 Redis 时序列化失败
- LoginUser 对象无法正确存储

**解决方案：**
创建 Redis 配置类 `mes-common-redis/src/main/java/com/mes/common/redis/config/RedisConfig.java`：

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        serializer.setObjectMapper(om);

        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

### 3.2 网关 Token 验证错误

**问题描述：**
```
认证失败: Invalid compact JWT string: Compact JWSs must contain exactly 2 period characters
```

**原因分析：**
- 网关直接使用 JWT token 作为 Redis key
- 实际应该解析 JWT 获取其中的 `login_user_key` 字段

**解决方案：**
修改 `AuthGlobalFilter.java`：

```java
// 错误方式
String userKey = "login_tokens:" + token;

// 正确方式
Claims claims = parseToken(token);
String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
String userKey = "login_tokens:" + uuid;
```

### 3.3 编译参数缺失

**问题描述：**
```
Name for argument of type [int] not specified, and parameter name information not available via reflection.
Ensure that the compiler uses the '-parameters' flag.
```

**解决方案：**
在 `pom.xml` 添加编译器配置：

```xml
<build>
    <pluginManagement>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <parameters>true</parameters>
                </configuration>
            </plugin>
        </plugins>
    </pluginManagement>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

**IDEA 配置：**
- File → Settings → Build → Compiler → Java Compiler
- Additional command line parameters: `-parameters`

### 3.4 MyBatis-Plus 分页失效

**问题描述：**
- 分页查询返回 total=0
- 分页功能不生效

**解决方案：**
创建 MyBatis-Plus 配置类：

```java
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

---

## 四、前端问题

### 4.1 路径别名未配置

**问题描述：**
```
Failed to resolve import "@/utils/request"
```

**解决方案：**
修改 `vite.config.js`：

```javascript
import path from 'path'

export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  }
})
```

### 4.2 API 路径重复

**问题描述：**
```
POST http://localhost:5176/api/api/system/user 404
```

**原因分析：**
- axios baseURL 已设置为 `/api`
- 请求路径又带了 `/api` 前缀

**解决方案：**
统一移除请求路径中的 `/api` 前缀：

```javascript
// 错误
request.get('/api/system/user/list')

// 正确
request.get('/system/user/list')
```

### 4.3 响应数据格式不匹配

**问题描述：**
- 登录后 token 存储错误
- 前端期望 `res.data`，实际返回 `res.data.data`

**解决方案：**
根据响应拦截器返回的数据结构调整：

```javascript
// 响应拦截器返回 response.data
// 即 {code: 200, msg: "成功", data: "token..."}

// Login.vue 中正确获取 token
localStorage.setItem('token', res.data)  // 而不是 res.data.data
```

---

## 五、Docker 构建问题

### 5.1 Dockerfile 依赖缺失

**问题描述：**
```
Child module /build/mes-services/mes-auth of /build/pom.xml does not exist
```

**原因分析：**
- Dockerfile 没有复制完整的 pom 文件结构
- Maven 无法解析多模块依赖

**解决方案：**
在 Dockerfile 中复制所有必要的 pom 文件：

```dockerfile
# 复制 pom 文件
COPY pom.xml .
COPY mes-common/pom.xml mes-common/
COPY mes-common/mes-common-core/pom.xml mes-common/mes-common-core/
COPY mes-common/mes-common-redis/pom.xml mes-common/mes-common-redis/
COPY mes-common/mes-common-security/pom.xml mes-common/mes-common-security/
COPY mes-common/mes-common-rabbitmq/pom.xml mes-common/mes-common-rabbitmq/
COPY mes-gateway/pom.xml mes-gateway/
COPY mes-services/mes-auth/pom.xml mes-services/mes-auth/
COPY mes-services/mes-system/pom.xml mes-services/mes-system/
```

---

## 六、快速排查指南

### 6.1 后端启动失败
1. 检查数据库连接配置
2. 检查 Redis 连接配置
3. 查看 IDEA 控制台错误日志

### 6.2 接口返回 401
1. 检查 localStorage 中是否存储了 token
2. 检查请求头是否携带 Authorization
3. 检查 Redis 中是否存在对应的登录信息

### 6.3 接口返回 404
1. 检查请求路径是否正确
2. 检查网关路由配置
3. 检查 Controller 的 @RequestMapping 路径

### 6.4 接口返回 500
1. 查看 IDEA 控制台错误日志
2. 检查数据库表是否存在
3. 检查 SQL 语句是否正确

---

## 七、常用调试命令

### 7.1 测试登录接口
```bash
curl -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'
```

### 7.2 测试带 Token 的接口
```bash
TOKEN="your-jwt-token"
curl http://localhost:9000/api/system/user/list \
  -H "Authorization: Bearer $TOKEN"
```

### 7.3 检查 Redis 中的 Token
```bash
docker exec mes-redis redis-cli KEYS "login_tokens:*"
docker exec mes-redis redis-cli GET "login_tokens:your-uuid"
```

### 7.4 检查数据库
```bash
mysql -u root -pmysql -e "SELECT * FROM mes_system.sys_user;"
```

---

## 八、默认账户信息

| 系统 | 用户名 | 密码 |
|------|--------|------|
| 管理后台 | admin | 123456 |
| 操作员 | operator | 123456 |

---

## 九、项目启动顺序

1. 启动基础设施
   ```bash
   docker-compose up -d mysql redis rabbitmq
   ```

2. 初始化数据库（首次）
   ```bash
   mysql -u root -pmysql < sql/init/local_init.sql
   ```

3. 启动后端服务（IDEA）
   - mes-auth (9001)
   - mes-system (9002)
   - mes-gateway (9000)

4. 启动前端
   ```bash
   cd mes-frontend
   npm install
   npm run dev
   ```

5. 访问系统
   - 前端地址: http://localhost:5173 (或 5174/5175/5176)
   - 网关地址: http://localhost:9000

---

*文档结束*
