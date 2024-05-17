FROM public.ecr.aws/docker/library/openjdk:17

ADD . /app
WORKDIR /app
COPY build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseSerialGC", "-Xms1024M", "-Xmx8192M", "-jar","app.jar"]