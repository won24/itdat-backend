FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY build/libs/back-0.0.1-SNAPSHOT.jar /opt/app/itdatss.jar
CMD ["java", "-jar", "/opt/app/itdatss.jar"]
EXPOSE 8082