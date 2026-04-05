FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY src/main/java/com/hisabsetu/hisabsetu .

RUN ./mvnw clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "target/*.jar"]