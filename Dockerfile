FROM eclipse-temurin:21.0.4_7-jdk-ubi9-minimal AS builder

WORKDIR /opt/moirai

COPY ./ ./

RUN microdnf install -y yum && \
    microdnf clean all && \
    yum update -y && \
    yum upgrade -y && \
    yum install -y maven && \
    mvn clean install -DskipTests

FROM eclipse-temurin:21.0.4_7-jre-ubi9-minimal AS runner

ENV DISCORD_BOT_CLIENT_ID=
ENV DISCORD_BOT_CLIENT_SECRET=
ENV DISCORD_BOT_REDIRECT_URL=
ENV DISCORD_BOT_API_TOKEN=
ENV OPENAI_API_TOKEN=
ENV SPRING_APPLICATION_PROFILES=
ENV POSTGRES_HOST=
ENV POSTGRES_DB=
ENV POSTGRES_USER=
ENV POSTGRES_PASSWORD=
ENV CHATRPG_LOG_LEVEL=

WORKDIR /opt/moirai

COPY --from=builder /opt/moirai/target/discordbot-2.1.0-SNAPSHOT.jar discordbot-2.1.0-SNAPSHOT.jar

EXPOSE 8080
EXPOSE 8000

CMD ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000", "-jar", "/opt/moirai/discordbot-2.1.0-SNAPSHOT.jar"]
