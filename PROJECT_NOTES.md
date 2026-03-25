# Vue + Spring Boot + Docker 全栈项目部署笔记

## 一、项目概览

本项目是一个完整的前后端分离应用，通过 Docker 容器化部署，并实现 CI/CD 自动化部署。

```
技术架构：
┌─────────────────────────────────────────────────────────┐
│                      用户浏览器                          │
│                    http://服务器IP                       │
└─────────────────────────┬───────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────┐
│                   Nginx (端口 80)                        │
│              静态资源 + 反向代理 /api                     │
└─────────────────────────┬───────────────────────────────┘
                          │
          ┌───────────────┴───────────────┐
          │                               │
          ▼                               ▼
┌─────────────────────┐         ┌─────────────────────┐
│   Vue 3 前端        │         │  Spring Boot 后端    │
│   (Nginx 托管)      │         │  (端口 8081)         │
└─────────────────────┘         └──────────┬──────────┘
                                           │
                                           ▼
                                 ┌─────────────────────┐
                                 │    MySQL 8.0        │
                                 │   (端口 3306)        │
                                 └─────────────────────┘
```

---

## 二、项目结构

```
myApp/
├── frontend/                    # 前端项目
│   ├── src/
│   │   ├── main.js              # Vue 入口文件
│   │   ├── App.vue              # 根组件
│   │   └── components/          # 组件目录
│   │       └── UserList.vue     # 用户列表组件
│   ├── index.html               # HTML 入口
│   ├── package.json             # npm 依赖配置
│   ├── vite.config.js           # Vite 构建配置
│   ├── nginx.conf               # Nginx 配置
│   └── Dockerfile               # 前端容器构建文件
│
├── backend/                     # 后端项目
│   ├── src/main/java/com/myapp/
│   │   ├── MyappApplication.java    # Spring Boot 启动类
│   │   ├── controller/
│   │   │   └── UserController.java  # REST 控制器
│   │   ├── entity/
│   │   │   └── User.java            # 实体类
│   │   └── repository/
│   │       └── UserRepository.java  # JPA 仓库
│   ├── src/main/resources/
│   │   └── application.yml          # 应用配置
│   ├── pom.xml                      # Maven 依赖配置
│   └── Dockerfile                   # 后端容器构建文件
│
├── mysql/
│   └── init.sql                 # 数据库初始化脚本
│
├── .github/workflows/
│   └── deploy.yml               # GitHub Actions 自动部署
│
├── docker-compose.yml           # Docker 编排文件
└── .gitignore                   # Git 忽略文件
```

---

## 三、前端详解

### 3.1 技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4 | 渐进式 JavaScript 框架 |
| Vite | 5.x | 下一代前端构建工具 |
| Axios | 1.6 | HTTP 请求库 |
| Nginx | Alpine | 高性能 Web 服务器 |

### 3.2 核心文件

**package.json** - 依赖配置
```json
{
  "dependencies": {
    "vue": "^3.4.21",
    "axios": "^1.6.7"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.4",
    "vite": "^5.1.6"
  }
}
```

**nginx.conf** - 反向代理配置
```nginx
server {
    listen 80;

    # 前端静态资源
    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    # API 反向代理到后端
    location /api/ {
        proxy_pass http://backend:8081/api/;
    }
}
```

### 3.3 Dockerfile（多阶段构建）
```dockerfile
# 阶段1: 构建
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 阶段2: 运行
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
```

---

## 四、后端详解

### 4.1 技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | LTS 版本 |
| Spring Boot | 3.2.3 | 快速开发框架 |
| Spring Data JPA | - | ORM 框架 |
| MySQL Connector | - | MySQL 驱动 |

### 4.2 核心文件

**pom.xml** - Maven 配置
```xml
<dependencies>
    <!-- Web 模块 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <!-- JPA 数据访问 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <!-- MySQL 驱动 -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
    </dependency>
</dependencies>
```

**application.yml** - 应用配置
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://mysql:3306/myapp
    username: root
    password: root123
  jpa:
    hibernate:
      ddl-auto: update  # 自动更新表结构
```

### 4.3 REST API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 获取所有用户 |
| POST | /api/users | 创建用户 |

### 4.4 Dockerfile（多阶段构建）
```dockerfile
# 阶段1: 构建
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn package -DskipTests

# 阶段2: 运行
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 五、Docker Compose 编排

```yaml
services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: myapp
    volumes:
      - mysql_data:/var/lib/mysql           # 数据持久化
      - ./mysql/init.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:                            # 健康检查
      test: ["CMD", "mysqladmin", "ping"]
      interval: 10s
      retries: 5

  # Spring Boot 后端
  backend:
    build: ./backend
    depends_on:
      mysql:
        condition: service_healthy          # 等待 MySQL 就绪
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/myapp

  # Vue 前端
  frontend:
    build: ./frontend
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  mysql_data:                               # 数据卷
```

### 5.1 服务依赖关系
```
mysql (先启动，健康检查通过)
   ↓
backend (等待 mysql healthy 后启动)
   ↓
frontend (最后启动)
```

---

## 六、CI/CD 自动部署

### 6.1 GitHub Actions 工作流

```yaml
name: Deploy to Server

on:
  push:
    branches: [main]          # 推送到 main 分支时触发

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Deploy via SSH
        uses: appleboy/ssh-action@v1.0.3
        with:
          host: ${{ secrets.SERVER_HOST }}
          username: ${{ secrets.SERVER_USER }}
          key: ${{ secrets.SSH_PRIVATE_KEY }}
          script: |
            cd ${{ secrets.DEPLOY_PATH }}
            git pull origin main
            docker-compose down
            docker-compose up --build -d
```

### 6.2 GitHub Secrets 配置

| Secret | 说明 | 示例 |
|--------|------|------|
| SERVER_HOST | 服务器 IP | 111.230.39.201 |
| SERVER_USER | SSH 用户名 | root |
| SSH_PRIVATE_KEY | SSH 私钥 | -----BEGIN... |
| DEPLOY_PATH | 项目路径 | /root/myApp |

### 6.3 部署流程
```
本地修改代码
    ↓
git push 到 GitHub
    ↓
GitHub Actions 自动触发
    ↓
SSH 连接服务器
    ↓
git pull 拉取最新代码
    ↓
docker-compose 重新构建部署
    ↓
应用更新完成
```

---

## 七、常用命令

### 7.1 本地开发

```bash
# 启动所有服务
docker-compose up --build -d

# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down

# 重新构建单个服务
docker-compose up --build -d backend
```

### 7.2 Git 操作

```bash
# 初始化仓库
git init

# 添加远程仓库
git remote add origin git@github.com:xxx/myApp.git

# 提交代码
git add .
git commit -m "更新说明"
git push origin main
```

### 7.3 服务器操作

```bash
# SSH 登录
ssh root@服务器IP

# 查看容器状态
docker-compose ps

# 查看后端日志
docker-compose logs -f backend

# 重启服务
docker-compose restart

# 完全重建
docker-compose down && docker-compose up --build -d
```

---

## 八、注意事项

### 8.1 端口配置
| 服务 | 容器端口 | 主机端口 |
|------|----------|----------|
| Frontend (Nginx) | 80 | 80 |
| Backend (Spring) | 8081 | 8081 |
| MySQL | 3306 | 3307 |

### 8.2 安全建议
1. 生产环境修改默认密码
2. 配置防火墙只开放必要端口
3. 使用 HTTPS (配置 SSL 证书)
4. 敏感信息使用环境变量

### 8.3 故障排查
```bash
# 查看容器日志
docker-compose logs backend

# 进入容器调试
docker exec -it myapp-backend sh

# 检查网络
docker network ls
docker network inspect myapp_app-network
```

---

## 九、扩展方向

1. **添加 Redis** - 缓存和会话管理
2. **添加 Nginx SSL** - HTTPS 支持
3. **添加监控** - Prometheus + Grafana
4. **添加日志收集** - ELK Stack
5. **添加消息队列** - RabbitMQ / Kafka
6. **添加对象存储** - MinIO / OSS

---

> 项目地址: https://github.com/Ray20184351106/myApp
>
> 服务器地址: http://111.230.39.201
