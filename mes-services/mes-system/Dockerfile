# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 复制 pom 文件
COPY pom.xml .
COPY mes-common/pom.xml mes-common/
COPY mes-common/mes-common-core/pom.xml mes-common/mes-common-core/
COPY mes-common/mes-common-redis/pom.xml mes-common/mes-common-redis/
COPY mes-common/mes-common-security/pom.xml mes-common/mes-common-security/
COPY mes-common/mes-common-rabbitmq/pom.xml mes-common/mes-common-rabbitmq/
COPY mes-services/mes-system/pom.xml mes-services/mes-system/

# 下载依赖
RUN mvn dependency:go-offline -B -pl mes-services/mes-system -am

# 复制源代码
COPY mes-common/mes-common-core/src mes-common/mes-common-core/src
COPY mes-common/mes-common-redis/src mes-common/mes-common-redis/src
COPY mes-common/mes-common-security/src mes-common/mes-common-security/src
COPY mes-common/mes-common-rabbitmq/src mes-common/mes-common-rabbitmq/src
COPY mes-services/mes-system/src mes-services/mes-system/src

# 构建
RUN mvn package -DskipTests -pl mes-services/mes-system -am

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/mes-services/mes-system/target/*.jar app.jar
EXPOSE 9002
ENTRYPOINT ["java", "-jar", "app.jar"]
