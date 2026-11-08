FROM maven:3-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src src
RUN mvn -B clean package -Pproduction -DskipTests

FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app

COPY --from=builder /app/target/maxwell-pbo2026-springboot-cashflow-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
