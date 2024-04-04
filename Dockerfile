FROM gradle:7.2.0-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon 

FROM openjdk:11-jre-slim

#EXPOSE 8080

#RUN mkdir /app

#COPY --from=build /home/gradle/src/build/libs/*.jar /app/spring-boot-application.jar

#ENTRYPOINT ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-Djava.security.egd=file:/dev/./urandom","-jar","/app/spring-boot-application.jar"]


# Use AdoptOpenJDK for base image.
# It's important to use OpenJDK 8u191 or above that has container support enabled.
# https://hub.docker.com/r/adoptopenjdk/openjdk8
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM adoptopenjdk/openjdk11:alpine-jre

# Copy the jar to the production image from the builder stage.
#COPY --from=build /home/gradle/src/build/libs/zf_security*.jar /zf_security.jar

# Run the web service on container startup.
# CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/zf_security.jar"]



COPY --from=build /home/gradle/src/build/libs/zf_workflow_core*.jar zf_workflow_core.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "zf_workflow_core.jar"]

