FROM adoptopenjdk/openjdk11:latest
WORKDIR /app
ARG JAR_FILE=target/gateway-1.0.jar
ADD ${JAR_FILE} .
ENTRYPOINT ["java","-jar","gateway-1.0.jar"]