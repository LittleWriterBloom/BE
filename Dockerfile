FROM amazoncorretto:17
ARG JAR_FILE=build/libs/littleWriter-0.0.1-dev.jar
ADD ${JAR_FILE} docker-littleWriter.jar
ENTRYPOINT ["java", "-jar", "/docker-littleWriter.jar"]
