# FROM openjdk:19-alpine AS builder

# WORKDIR /opt/chatrpg
# ADD ./pom.xml pom.xml
# ADD ./src src/
# RUN apk add -u maven &&\
#     mvn clean package -e

FROM openjdk:19-alpine

WORKDIR /opt/chatrpg
# COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

COPY target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
