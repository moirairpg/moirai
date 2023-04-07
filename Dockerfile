# FROM openjdk:17-alpine AS builder
FROM eclipse-temurin:17.0.6_10-jdk-jammy AS builder

WORKDIR /opt/chatrpg

ADD ./ ./

RUN ./mvnw clean package -e -DskipTests -DskipFormat=true

FROM eclipse-temurin:17.0.6_10-jre-jammy

WORKDIR /opt/chatrpg

COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
