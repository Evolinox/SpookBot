FROM amazoncorretto:11
LABEL description="SpookBot Docker Image"
LABEL version="1.1"
LABEL author="Evolinox"

WORKDIR /app
COPY . /app

RUN yum install -y maven && \
    mvn clean package

CMD ["java", "-cp", "target/SpookBot-1.1-jar-with-dependencies.jar", "SpookBot.Main"]