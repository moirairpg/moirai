FROM registry.access.redhat.com/ubi8/openjdk-17

WORKDIR /opt/chatrpg

# libgcc gcompat libc6-compat

COPY target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
