FROM eclipse-temurin:17.0.6_10-jre-jammy

WORKDIR /opt/moirai

COPY ./target/discordbot-0.0.1-SNAPSHOT.jar discordbot-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/moirai/discordbot-0.0.1-SNAPSHOT.jar"]
