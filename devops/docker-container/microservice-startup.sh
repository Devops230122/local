#!/usr/bin/env sh
echo "Starting up microservice 'Localization service'"
echo "JAVA_ARGS=" $JAVA_ARGS
java $JAVA_ARGS -javaagent:applicationinsights-agent-3.2.11.jar -jar localization-service.jar "$@"
