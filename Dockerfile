FROM openjdk:17
EXPOSE 8080
ADD target/training-user-service-docker.jar training-user-service-docker.jar
ENTRYPOINT ["java","-jar","/training-user-service-docker.jar"]