FROM maven:3.8.4-openjdk-17 as maven-builder

COPY src /app/src
COPY pom.xml /app
COPY .mvn/plugins/eclipse-java-google-style.xml /app/.mvn/plugins/
COPY .mvn/plugins/spotbugs-exclude-filter.xml /app/.mvn/plugins/
COPY .mvn/plugins/pmd/maven-pmd-plugin-custom-rules.xml /app/.mvn/plugins/pmd/
COPY devops/settings.xml /root/.m2/

RUN mvn -f /app/pom.xml clean package -DskipTests

FROM openjdk:17-alpine

COPY --from=maven-builder app/target/localization-service-*.jar /localization-service/localization-service.jar
COPY devops/docker-container/microservice-startup.sh /localization-service
COPY devops/applicationinsights/applicationinsights-agent-3.2.11.jar /localization-service/applicationinsights-agent-3.2.11.jar
COPY devops/applicationinsights/applicationinsights.json /localization-service/applicationinsights.json

WORKDIR /localization-service

RUN chmod u+x microservice-startup.sh

# Expose ports
# 8080: application port
EXPOSE 8080

ENTRYPOINT ["/localization-service/microservice-startup.sh"]
