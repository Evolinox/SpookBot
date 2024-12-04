FROM amazoncorretto:11
LABEL authors="Evolinox"

WORKDIR /app
COPY . /app

RUN yum install -y maven && \
    mvn clean package

CMD ["java", "-cp", "target/SpookBot-1.1.jar", "SpookBot.Main"]