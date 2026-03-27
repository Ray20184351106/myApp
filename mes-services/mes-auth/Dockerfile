# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 配置阿里云 Maven 镜像加速
COPY docker/maven/settings.xml /root/.m2/settings.xml

# 复制根 pom.xml
COPY pom.xml .

# 复制公共模块 pom.xml
COPY mes-common/pom.xml mes-common/
COPY mes-common/mes-common-core/pom.xml mes-common/mes-common-core/
COPY mes-common/mes-common-redis/pom.xml mes-common/mes-common-redis/
COPY mes-common/mes-common-security/pom.xml mes-common/mes-common-security/
COPY mes-common/mes-common-rabbitmq/pom.xml mes-common/mes-common-rabbitmq/

# 复制网关 pom.xml
COPY mes-gateway/pom.xml mes-gateway/

# 复制微服务 pom.xml
COPY mes-services/mes-auth/pom.xml mes-services/mes-auth/
COPY mes-services/mes-system/pom.xml mes-services/mes-system/

# 下载依赖（使用阿里云镜像，速度更快）
RUN mvn dependency:go-offline -B -pl mes-services/mes-auth -am || mvn dependency:resolve -B -pl mes-services/mes-auth -am || true

# 复制源代码
COPY mes-common/mes-common-core/src mes-common/mes-common-core/src
COPY mes-common/mes-common-redis/src mes-common/mes-common-redis/src
COPY mes-common/mes-common-security/src mes-common/mes-common-security/src
COPY mes-common/mes-common-rabbitmq/src mes-common/mes-common-rabbitmq/src
COPY mes-services/mes-auth/src mes-services/mes-auth/src

# 构建
RUN mvn package -DskipTests -pl mes-services/mes-auth -am

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/mes-services/mes-auth/target/*.jar app.jar
EXPOSE 9001
ENTRYPOINT ["java", "-jar", "app.jar"]