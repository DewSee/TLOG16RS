FROM frolvlad/alpine-oraclejdk8:slim
MAINTAINER Andras Gazdag "agazdag@precognox.com"
RUN apk --update add curl tar bash wget openssl
RUN wget --no-check-certificate https://github.com/jwilder/dockerize/releases/download/v0.2.0/dockerize-linux-amd64-v0.2.0.tar.gz
RUN tar -C /usr/local/bin -xzvf dockerize-linux-amd64-v0.2.0.tar.gz
ADD distributions/TLOG16RS-0.1.0-SNAPSHOT.tar /tmp
RUN mv /tmp/TLOG16RS-0.1.0-SNAPSHOT /TLOG16RS
WORKDIR /
CMD ["bash", "-c", "dockerize -wait ${DB_URL/jdbc:mariadb/tcp} -template /TLOG16RS/config.yml.template:/TLOG16RS/config.yml /TLOG16RS/bin/TLOG16RS   server TLOG16RS/config.yml"]
