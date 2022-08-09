FROM openjdk:17-alpine

# Install Chrome
RUN apk --update add chromium
RUN apk --update add chromium-chromedriver

ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]