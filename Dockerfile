FROM openjdk:19-alpine

WORKDIR /opt/chatrpg

RUN apk upgrade --no-cache && \
    apk add --no-cache libgcc

COPY target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
