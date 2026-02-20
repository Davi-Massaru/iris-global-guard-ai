# =========================
# Build stage
# =========================
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

COPY . .

RUN chmod +x mvnw

# Instala JDBC IRIS no repositório local do build
RUN ./mvnw install:install-file \
    -Dfile=src/main/resources/lib/intersystems-jdbc-3.7.1.jar \
    -DgroupId=com.intersystems \
    -DartifactId=intersystems-jdbc \
    -Dversion=3.7.1 \
    -Dpackaging=jar \
    -DgeneratePom=true

# Build Quarkus JVM
RUN ./mvnw package -DskipTests

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/quarkus-app/lib/ /app/lib/
COPY --from=build /build/target/quarkus-app/*.jar /app/
COPY --from=build /build/target/quarkus-app/app/ /app/app/
COPY --from=build /build/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]