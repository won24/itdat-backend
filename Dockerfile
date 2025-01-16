FROM eclipse-temurin:17
RUN mkdir /opt/app
COPY build/libs/back-0.0.1-SNAPSHOT.jar /opt/app/itdat.jar
CMD ["java", "-jar", "/opt/app/itdat.jar"]
EXPOSE 8082