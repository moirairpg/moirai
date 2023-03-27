# FROM openjdk:17-alpine AS builder
FROM eclipse-temurin:17.0.6_10-jdk-jammy AS builder

WORKDIR /opt/chatrpg

ADD ./pom.xml pom.xml
ADD ./src src/

RUN apt update -y &&\
    apt install -y wget &&\
    wget https://dlcdn.apache.org/maven/maven-3/3.9.0/binaries/apache-maven-3.9.0-bin.tar.gz -P /tmp &&\
    wget https://gist.githubusercontent.com/thaalesalves/6aaf8d0601a777593888415558e7f6d4/raw/channel-config.yaml -P src/main/resources &&\
    wget https://gist.githubusercontent.com/thaalesalves/bdca44fd096d91d0e85a9de00e15dfb8/raw/worlds.yaml -P src/main/resources &&\
    tar xf /tmp/apache-maven-*.tar.gz -C /opt &&\
    ln -s /opt/apache-maven-3.9.0 /opt/maven &&\
    /opt/maven/bin/mvn clean package -e -DskipTests

FROM eclipse-temurin:17.0.6_10-jre-jammy

WORKDIR /opt/chatrpg

COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
