# FROM openjdk:17-alpine AS builder
FROM eclipse-temurin:18.0.1_10-jdk-jammy AS builder

WORKDIR /opt/chatrpg

# RUN apk upgrade --no-cache && \ 
#         apk add --no-cache libgcc libstdc++ ncurses-libs gcompat libc6-compat

ADD ./pom.xml pom.xml
ADD ./src src/
# RUN apk add -u maven &&\
#     mv src/main/resources/bot-config-sample.yaml src/main/resources/bot-config.yaml &&\
#     mvn clean package -e

RUN apt update -y &&\
    apt install -y wget &&\
    wget https://dlcdn.apache.org/maven/maven-3/3.9.0/binaries/apache-maven-3.9.0-bin.tar.gz -P /tmp &&\
    tar xf /tmp/apache-maven-*.tar.gz -C /opt &&\
    ln -s /opt/apache-maven-3.9.0 /opt/maven &&\
    mv src/main/resources/bot-config-sample.yaml src/main/resources/bot-config.yaml &&\
    /opt/maven/bin/mvn clean package -e

#eclipse-temurin:18.0.1_10-jdk-jammy
#eclipse-temurin:18.0.1_10-jre-jammy
FROM openjdk:17-alpine

WORKDIR /opt/chatrpg


COPY --from=builder /opt/chatrpg/target/chatrpg-0.0.1-SNAPSHOT.jar chatrpg-0.0.1-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "/opt/chatrpg/chatrpg-0.0.1-SNAPSHOT.jar"]
