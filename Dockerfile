FROM amazoncorretto:17
WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} cms-app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Dspring.profiles.active=prod","-jar","/app/cms-app.jar"]