FROM java:8-jre

EXPOSE 80
COPY "build/flippy-standalone-0.1.2-SNAPSHOT.jar" "/usr/src"
ENTRYPOINT [ "java", "-jar", "/usr/src/flippy-standalone-0.1.2-SNAPSHOT.jar" ]
