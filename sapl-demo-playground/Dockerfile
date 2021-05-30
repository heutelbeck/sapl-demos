FROM adoptopenjdk/openjdk11:alpine
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring
VOLUME /tmp
VOLUME /pdp/lib
VOLUME /etc/config
WORKDIR /pdp
ARG JAR_FILE
ADD ${JAR_FILE} /playground/playground.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-jar","/playground/playground.jar"]