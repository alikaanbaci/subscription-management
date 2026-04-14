FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /workspace

COPY pom.xml .
COPY shared-contracts/pom.xml shared-contracts/pom.xml
COPY subscription-service/pom.xml subscription-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml
COPY notification-service/pom.xml notification-service/pom.xml

RUN mvn -q -pl payment-service -am dependency:go-offline

COPY shared-contracts/src shared-contracts/src
COPY payment-service/src payment-service/src

RUN mvn -q -pl payment-service -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/payment-service/target/payment-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
