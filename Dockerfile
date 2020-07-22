FROM openjdk:11
ARG JAR_FILE=target/demo-app.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]