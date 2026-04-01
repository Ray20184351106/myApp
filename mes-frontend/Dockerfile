# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# 先复制 package 文件并安装依赖（利用 Docker 缓存层）
COPY package.json package-lock.json ./
RUN npm ci --registry=https://registry.npmmirror.com --prefer-offline --no-audit --progress=false

# 复制源代码
COPY . .

# 使用 Node 多核构建加速
ENV NODE_OPTIONS="--max-old-space-size=2048"
RUN npm run build

# Run stage
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

# 添加健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
