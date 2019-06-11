FROM anapsix/alpine-java:8_server-jre_unlimited

RUN mkdir -p /app-proxy

WORKDIR /app-proxy

EXPOSE 7070

ADD ./app-proxy/target/app-proxy-0.0.1-SNAPSHOT.jar ./

CMD java -Djava.security.egd=file:/dev/./urandom -jar app-proxy-0.0.1-SNAPSHOT.jar