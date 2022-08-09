FROM openjdk:17-alpine

# Install Chrome
RUN apt-get install -y wget
RUN wget -q https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN apt-get install ./google-chrome-stable_current_amd64.deb

# Install Chrome Driver
RUN apt-get install unzip curl && \
	wget -O "/tmp/chromedriver.zip" "http://chromedriver.storage.googleapis.com/`curl -sS chromedriver.storage.googleapis.com/LATEST_RELEASE`/chromedriver_linux64.zip" && \
	unzip /tmp/chromedriver.zip chromedriver -d /usr/bin

ARG JAR_FILE=*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]