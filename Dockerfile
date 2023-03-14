# FROM openjdk:17-alpine AS builder
FROM eclipse-temurin:18.0.1_10-jdk-jammy AS builder

WORKDIR /opt/chatrpg

ADD ./pom.xml pom.xml
ADD ./src src/

RUN apt update -y &&\
    apt install -y wget &&\
    wget https://dlcdn.apache.org/maven/maven-3/3.9.0/binaries/apache-maven-3.9.0-bin.tar.gz -P /tmp &&\
    tar xf /tmp/apache-maven-*.tar.gz -C /opt &&\
    ln -s /opt/apache-maven-3.9.0 /opt/maven &&\
    /opt/maven/bin/mvn clean package -e

#eclipse-temurin:18.0.1_10-jdk-jammy
#eclipse-temurin:18.0.1_10-jre-jammy
FROM eclipse-temurin:18.0.1_10-jre-jammy

WORKDIR /opt/chatrpg

COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
