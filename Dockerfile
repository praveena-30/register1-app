FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the JAR file that was built locally
COPY target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"] 