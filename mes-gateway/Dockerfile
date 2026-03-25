# Build stage
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# 复制 pom 文件
COPY pom.xml .
COPY mes-common/pom.xml mes-common/
COPY mes-common/mes-common-core/pom.xml mes-common/mes-common-core/
COPY mes-gateway/pom.xml mes-gateway/

# 下载依赖
RUN mvn dependency:go-offline -B -pl mes-gateway -am

# 复制源代码
COPY mes-common/mes-common-core/src mes-common/mes-common-core/src
COPY mes-gateway/src mes-gateway/src

# 构建
RUN mvn package -DskipTests -pl mes-gateway -am

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /build/mes-gateway/target/*.jar app.jar
EXPOSE 9000
ENTRYPOINT ["java", "-jar", "app.jar"]
