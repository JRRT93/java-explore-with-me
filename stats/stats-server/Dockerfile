FROM openjdk:19-jdk-alpine
COPY target/*.jar stats.jar
ENTRYPOINT ["java","-jar","/stats.jar"]