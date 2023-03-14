FROM openjdk:17-alpine AS builder

WORKDIR /opt/chatrpg
ADD ./pom.xml pom.xml
ADD ./src src/
RUN apk add -u maven &&\
    mv src/main/resources/bot-config-sample.yaml src/main/resources/bot-config.yaml &&\
    mvn clean package -e

FROM registry.access.redhat.com/ubi8/openjdk-17

WORKDIR /opt/chatrpg

RUN yum update -y &&\
    yum install -y libgcc libstdc++

COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
