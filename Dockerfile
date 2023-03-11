FROM openjdk:17-alpine AS builder

WORKDIR /opt/chatrpg
ADD ./pom.xml pom.xml
ADD ./src src/
RUN apk add -u maven &&\
    mv src/main/resources/bot-config-sample.yaml src/main/resources/bot-config.yaml &&\
    mvn clean package -e

FROM openjdk:17-alpine

WORKDIR /opt/chatrpg
COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
