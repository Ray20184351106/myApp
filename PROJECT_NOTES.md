# MES 制造执行系统 - 项目开发笔记

> 最后更新：2026-04-22

---

## 一、项目概览

本项目是一个基于微服务架构的 MES（制造执行系统），采用前后端分离开发模式，支持 Docker 容器化部署。

```
技术架构：
┌─────────────────────────────────────────────────────────┐
│                      用户浏览器                          │
│                  http://localhost:5173                   │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│              mes-gateway (API 网关, 端口 9000)           │
│           JWT 认证 + 路由转发 + Redis Token 验证         │
└─────────────────────────┬───────────────────────────────┘
                          │
          ┌───────────────┴───────────────┐
          │                               │
          ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│   mes-auth (9001)   │         │  mes-system (9002)  │
│     认证服务         │         │     系统服务         │
│   登录/注册/Token    │         │  用户/字典/配置管理   │
└─────────┬───────────┘         └──────────┬──────────┘
          │                                │
          └────────────────┬───────────────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
┌─────────────────┐ ┌───────────┐ ┌─────────────────┐
│   MySQL 8.0     │ │  Redis 7  │ │   RabbitMQ      │
│  (端口 3306)    │ │ (端口 6379)│ │ (5672/15672)    │
└─────────────────┘ └───────────┘ └─────────────────┘
```

---

## 二、技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | Java 运行环境 |
| Spring Boot | 3.2.3 | 基础框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Gateway | - | API 网关 |
| MyBatis-Plus | 3.5.5 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7 | 缓存中间件 |
| RabbitMQ | 3.12 | 消息队列 |
| JWT | 0.12.5 | 认证授权 |
| Hutool | 5.8.26 | 工具类库 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.21 | 前端框架 |
| Element Plus | 2.6.1 | UI 组件库 |
| Vite | 5.1.6 | 构建工具 |
| Pinia | 2.1.7 | 状态管理 |
| Vue Router | 4.3.0 | 路由管理 |
| Axios | 1.6.7 | HTTP 客户端 |

---

## 三、项目结构

```
mes/
├── pom.xml                          # Maven 父 POM
├── docker-compose.yml               # Docker 编排配置
│
├── mes-common/                      # 公共模块
│   ├── mes-common-core/             # 核心组件 (Result, Exception, Constants)
│   ├── mes-common-redis/            # Redis 配置和工具类
│   ├── mes-common-security/         # 安全模块 (JWT, TokenService)
│   └── mes-common-rabbitmq/         # RabbitMQ 配置
│
├── mes-gateway/                     # API 网关服务 (端口:9000)
│   └── src/main/java/com/mes/gateway/
│       ├── MesGatewayApplication.java
│       ├── filter/AuthGlobalFilter.java
│       └── config/CorsConfig.java
│
├── mes-services/                    # 微服务模块
│   ├── mes-auth/                    # 认证服务 (端口:9001)
│   │   └── src/main/java/com/mes/auth/
│   │       ├── controller/AuthController.java
│   │       ├── service/AuthService.java
│   │       ├── mapper/SellerUserMapper.java
│   │       └── entity/
│   │
│   └── mes-system/                  # 系统服务 (端口:9002)
│       └── src/main/java/com/mes/system/
│           ├── controller/
│           │   ├── SysUserController.java
│           │   ├── SysConfigController.java
│           │   └── SysDictController.java
│           ├── service/
│           ├── entity/
│           └── config/MybatisPlusConfig.java
│
├── mes-frontend/                    # 前端项目
│   ├── src/
│   │   ├── views/                   # 页面组件
│   │   │   ├── Login.vue            # 登录页
│   │   │   ├── Dashboard.vue        # 仪表盘
│   │   │   └── system/              # 系统管理
│   │   │       └── User.vue         # 用户管理
│   │   ├── layout/                  # 布局组件
│   │   ├── router/                  # 路由配置
│   │   ├── utils/request.js         # Axios 封装
│   │   ├── App.vue
│   │   └── main.js
│   ├── package.json
│   ├── vite.config.js
│   └── Dockerfile
│
├── sql/                             # 数据库脚本
│   └── init/
│       ├── 01_nacos_config.sql
│       ├── 02_mes_auth.sql
│       ├── 03_mes_system.sql
│       └── local_init.sql           # 本地开发初始化脚本
│
└── docs/                            # 文档
    ├── PROJECT_STRUCTURE.md         # 项目结构文档
    └── ISSUE_FIX_GUIDE.md           # 问题修复指南
```

---

## 四、开发进度

### 4.1 已完成功能 ✅

#### 后端服务
- [x] 微服务架构搭建
- [x] 公共模块开发 (core, redis, security, rabbitmq)
- [x] API 网关配置和路由
- [x] JWT Token 认证机制
- [x] Redis Token 存储
- [x] 用户登录/登出接口
- [x] 用户管理 CRUD 接口
- [x] 角色管理 CRUD 接口
- [x] 菜单管理 CRUD 接口
- [x] 用户角色关联接口
- [x] 角色菜单授权接口
- [x] 权限校验注解和切面 (@RequiresPermissions, @RequiresRoles)
- [x] 动态菜单路由接口
- [x] MyBatis-Plus 分页配置

#### 前端功能
- [x] Vue 3 项目框架
- [x] Element Plus UI 集成
- [x] 登录页面
- [x] 用户管理页面 (列表、新增、编辑、删除、分配角色)
- [x] 角色管理页面 (列表、新增、编辑、删除、菜单授权)
- [x] 菜单管理页面 (树形列表、新增、编辑、删除)
- [x] 动态菜单加载 (从后端获取)
- [x] 权限控制指令 (v-permission, v-role)
- [x] 登录过期自动跳转
- [x] Axios 请求封装
- [x] 多环境配置支持
- [x] Pinia 状态管理

#### 基础设施
- [x] Docker Compose 配置
- [x] MySQL 数据库初始化脚本
- [x] Redis 缓存配置
- [x] RabbitMQ 消息队列配置

### 4.2 待开发功能 🚧

#### 系统管理
- [ ] 部门管理
- [ ] 字典管理
- [ ] 配置管理
- [ ] 操作日志
- [ ] 在线用户监控

#### MES 核心业务
- [ ] 工单管理
- [ ] 工艺路线
- [ ] 设备管理
- [ ] 质量检验
- [ ] 数据采集

#### 前端功能
- [ ] 仪表盘数据展示
- [ ] 多标签页
- [ ] 个人中心页面

---

## 五、服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| mes-gateway | 9000 | API 网关 |
| mes-auth | 9001 | 认证服务 |
| mes-system | 9002 | 系统服务 |
| mes-frontend | 5173 | 前端服务 (开发) |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672 | 消息队列 |
| RabbitMQ Management | 15672 | 管理界面 |

---

## 六、快速开始

### 6.1 本地开发启动

```bash
# 1. 启动基础设施 (使用 Docker)
docker-compose up -d mysql redis rabbitmq

# 2. 初始化数据库 (首次)
mysql -u root -p < sql/init/local_init.sql

# 3. 在 IDEA 启动后端服务 (按顺序)
#    - mes-auth (Active profiles: local)
#    - mes-system (Active profiles: local)
#    - mes-gateway (Active profiles: local)

# 4. 启动前端
cd mes-frontend
npm install
npm run dev

# 5. 访问系统
#    前端: http://localhost:5173
#    账户: admin / 123456
```

### 6.2 Docker 部署

```bash
# 构建并启动所有服务
docker-compose up --build -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f mes-auth
```

---

## 七、API 接口

### 7.1 认证接口 (/api/auth)

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /login | 用户登录 |
| POST | /register | 用户注册 |
| POST | /logout | 用户登出 |
| GET | /getInfo | 获取用户信息（含权限、角色） |
| GET | /getRouters | 获取用户菜单路由 |

### 7.2 用户接口 (/api/system/user)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /list | 分页查询用户 |
| GET | /{id} | 获取用户详情 |
| POST | / | 新增用户 |
| PUT | / | 修改用户 |
| DELETE | /{id} | 删除用户 |
| GET | /{id}/roles | 获取用户角色列表 |
| PUT | /roles | 分配角色给用户 |

### 7.3 角色接口 (/api/system/role)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /list | 分页查询角色 |
| GET | /all | 获取所有角色 |
| GET | /{id} | 获取角色详情 |
| POST | / | 新增角色 |
| PUT | / | 修改角色 |
| DELETE | /{id} | 删除角色 |
| GET | /{id}/menus | 获取角色菜单列表 |
| PUT | /menus | 分配菜单给角色 |

### 7.4 菜单接口 (/api/system/menu)

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /list | 获取菜单列表 |
| GET | /tree | 获取菜单树 |
| GET | /{id} | 获取菜单详情 |
| POST | / | 新增菜单 |
| PUT | / | 修改菜单 |
| DELETE | /{id} | 删除菜单 |

---

## 八、配置说明

### 8.1 多环境配置

每个服务都有三个配置文件：

| 文件 | 环境 | 说明 |
|------|------|------|
| application.yml | 通用 | 公共配置 |
| application-local.yml | 本地开发 | localhost 连接 |
| application-docker.yml | Docker | 容器网络连接 |

### 8.2 数据库连接配置

**本地开发 (application-local.yml):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mes_auth
    username: root
    password: mysql
  data:
    redis:
      host: localhost
```

**Docker 环境 (application-docker.yml):**
```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql:3306/mes_auth
  data:
    redis:
      host: redis
```

---

## 九、默认账户

| 系统 | 用户名 | 密码 | 角色 |
|------|--------|------|------|
| 管理后台 | admin | 123456 | 超级管理员 |
| 操作员 | operator | 123456 | 操作员 |

---

## 十、常见问题

详见 [问题修复指南](./docs/ISSUE_FIX_GUIDE.md)

### 10.1 快速排查

| 问题 | 排查步骤 |
|------|----------|
| 后端启动失败 | 检查数据库/Redis 连接配置 |
| 接口返回 401 | 检查 Token 是否正确携带 |
| 接口返回 404 | 检查请求路径和网关路由 |
| 接口返回 500 | 查看 IDEA 控制台错误日志 |
| 前端路径错误 | 检查 vite.config.js 别名配置 |

### 10.2 调试命令

```bash
# 测试登录接口
curl -X POST http://localhost:9000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 检查 Redis Token
docker exec mes-redis redis-cli KEYS "login_tokens:*"

# 检查数据库
mysql -u root -pmysql -e "SELECT * FROM mes_system.sys_user;"
```

---

## 十一、服务器部署

详见 [服务器 MySQL 安装配置指南](./docs/SERVER_DEPLOY.md)

### 11.1 部署检查清单

- [ ] 数据库用户（不使用 root）
- [ ] 数据库强密码
- [ ] Redis 密码
- [ ] JWT Secret 更换
- [ ] 防火墙配置
- [ ] SSL 证书（生产环境）

---

## 十二、权限控制

### 12.1 权限标识规范

权限标识格式：`模块:资源:操作`

| 模块 | 权限标识 | 说明 |
|------|----------|------|
| 用户管理 | system:user:list | 查看用户列表 |
| 用户管理 | system:user:query | 查询用户详情 |
| 用户管理 | system:user:add | 新增用户 |
| 用户管理 | system:user:edit | 编辑用户 |
| 用户管理 | system:user:remove | 删除用户 |
| 角色管理 | system:role:list | 查看角色列表 |
| 角色管理 | system:role:add | 新增角色 |
| 角色管理 | system:role:edit | 编辑角色 |
| 角色管理 | system:role:remove | 删除角色 |
| 菜单管理 | system:menu:list | 查看菜单列表 |
| 菜单管理 | system:menu:add | 新增菜单 |
| 菜单管理 | system:menu:edit | 编辑菜单 |
| 菜单管理 | system:menu:remove | 删除菜单 |

### 12.2 后端权限注解

```java
// 单个权限
@RequiresPermissions("system:user:add")
public Result<Void> add(@RequestBody User user) { ... }

// 多个权限（AND关系）
@RequiresPermissions(value = {"system:user:add", "system:user:edit"}, logical = Logical.AND)

// 多个权限（OR关系）
@RequiresPermissions(value = {"system:user:add", "system:user:edit"}, logical = Logical.OR)

// 角色校验
@RequiresRoles("admin")
```

### 12.3 前端权限指令

```vue
<!-- 单个权限 -->
<el-button v-permission="'system:user:add'">新增</el-button>

<!-- 多个权限 -->
<el-button v-permission="['system:user:add', 'system:user:edit']">操作</el-button>
```

### 12.4 超级管理员

拥有 `*:*:*` 权限或 `admin` 角色的用户自动拥有所有权限。

---

## 十三、Git 提交规范

使用 Conventional Commits 规范：

| 前缀 | 说明 |
|------|------|
| feat: | 新功能 |
| fix: | Bug 修复 |
| docs: | 文档更新 |
| refactor: | 代码重构 |
| chore: | 构建/工具变动 |
| style: | 代码格式 |
| test: | 测试相关 |

---

> 项目地址: https://github.com/Ray20184351106/mes
