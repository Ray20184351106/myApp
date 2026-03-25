# MES 制造执行系统 - 项目结构文档

## 项目概述

本项目是一个基于微服务架构的MES（制造执行系统），采用前后端分离开发模式，支持Docker容器化部署。

## 技术栈

### 后端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| JDK | 17 | Java运行环境 |
| Spring Boot | 3.2.3 | 基础框架 |
| Spring Cloud | 2023.0.0 | 微服务框架 |
| Spring Cloud Gateway | - | API网关 |
| MyBatis-Plus | 3.5.5 | ORM框架 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7 | 缓存中间件 |
| RabbitMQ | 3.12 | 消息队列 |
| JWT | - | 认证授权 |

### 前端技术
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4.21 | 前端框架 |
| Element Plus | 2.6.1 | UI组件库 |
| Vite | 5.1.6 | 构建工具 |
| Pinia | 2.1.7 | 状态管理 |
| Vue Router | 4.3.0 | 路由管理 |
| Axios | 1.6.7 | HTTP客户端 |

## 项目结构

```
mes/
├── pom.xml                          # Maven父POM
├── docker-compose.yml               # Docker编排配置
│
├── mes-common/                      # 公共模块
│   ├── mes-common-core/             # 核心组件
│   │   └── src/main/java/com/mes/common/core/
│   │       ├── result/              # 统一返回结果
│   │       ├── exception/           # 全局异常处理
│   │       ├── constant/            # 常量定义
│   │       └── utils/               # 工具类
│   │
│   ├── mes-common-redis/            # Redis配置
│   │   └── src/main/java/com/mes/common/redis/
│   │       └── service/RedisService.java
│   │
│   ├── mes-common-security/         # 安全模块
│   │   └── src/main/java/com/mes/common/security/
│   │       ├── service/TokenService.java    # JWT Token服务
│   │       ├── AuthInterceptor.java         # 认证拦截器
│   │       ├── SecurityConfig.java          # 安全配置
│   │       └── entity/LoginUser.java        # 登录用户实体
│   │
│   └── mes-common-rabbitmq/         # RabbitMQ配置
│
├── mes-gateway/                     # API网关服务 (端口:9000)
│   └── src/main/java/com/mes/gateway/
│       ├── MesGatewayApplication.java       # 启动类
│       ├── filter/AuthGlobalFilter.java     # 认证过滤器
│       ├── config/CorsConfig.java           # 跨域配置
│       └── handler/GlobalExceptionHandler.java
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
│           ├── service/
│           └── entity/
│
├── mes-frontend/                    # 前端项目
│   ├── src/
│   │   ├── views/                   # 页面组件
│   │   │   ├── Login.vue            # 登录页
│   │   │   └── Dashboard.vue        # 仪表盘
│   │   ├── layout/                  # 布局组件
│   │   ├── router/                  # 路由配置
│   │   ├── App.vue                  # 根组件
│   │   └── main.js                  # 入口文件
│   ├── package.json
│   ├── vite.config.js
│   └── Dockerfile
│
└── sql/                             # 数据库脚本
    └── init/
        ├── 01_nacos_config.sql      # Nacos配置库
        ├── 02_mes_auth.sql          # 认证服务数据库
        └── 03_mes_system.sql        # 系统服务数据库
```

## 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| mes-gateway | 9000 | API网关 |
| mes-auth | 9001 | 认证服务 |
| mes-system | 9002 | 系统服务 |
| mes-frontend | 80 | 前端服务 |
| MySQL | 3307 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672/15672 | 消息队列 |

## API路由

| 路径 | 服务 | 说明 |
|------|------|------|
| /api/auth/** | mes-auth | 认证相关API |
| /api/system/** | mes-system | 系统管理API |

## 认证流程

```
1. 用户登录 POST /api/auth/login
   ↓
2. Gateway路由到mes-auth服务
   ↓
3. AuthService验证用户名密码(BCrypt)
   ↓
4. TokenService生成JWT Token
   ↓
5. 用户信息存入Redis
   ↓
6. 返回Token给前端
   ↓
7. 后续请求携带Token，Gateway验证
```

## 数据库设计

### mes_auth 数据库
- `sys_user` - 用户表
- `sys_role` - 角色表
- `sys_user_role` - 用户角色关联
- `sys_menu` - 菜单表
- `sys_role_menu` - 角色菜单关联

### 默认账户
- admin / 123456 (超级管理员)
- operator / 123456 (操作员)

## 快速开始

```bash
# 1. 启动基础设施
docker-compose up -d mysql redis rabbitmq

# 2. 构建后端服务
mvn clean package -DskipTests

# 3. 启动后端服务
java -jar mes-services/mes-auth/target/mes-auth-1.0.0.jar
java -jar mes-services/mes-system/target/mes-system-1.0.0.jar
java -jar mes-gateway/target/mes-gateway-1.0.0.jar

# 4. 启动前端
cd mes-frontend
npm install
npm run dev

# 5. 访问系统
http://localhost
```

## 开发状态

### 已完成
- [x] 微服务架构搭建
- [x] 公共模块开发
- [x] 网关配置
- [x] 认证服务框架
- [x] 前端框架搭建
- [x] Docker配置

### 待开发
- [ ] 完善登录认证功能
- [ ] 用户管理CRUD
- [ ] 角色权限管理
- [ ] 菜单管理
- [ ] MES核心业务模块

## 提交规范

使用 Conventional Commits 规范：
- `feat:` 新功能
- `fix:` Bug修复
- `docs:` 文档更新
- `refactor:` 代码重构
- `chore:` 构建/工具变动
