FROM eclipse-temurin:17.0.6_10-jre-jammy

WORKDIR /opt/chatrpg

COPY ./target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

# CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
RUN apt update -y && \
    apt install jq -y && \
    java -jar /opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar

CMD ["tail", "-f", "/opt/chatrpg/logs/bot.log", "|", "jq", "-R", "fromjson?"]