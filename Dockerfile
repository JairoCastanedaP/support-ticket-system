FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

COPY . .
# Las pruebas se ejecutan en la construcción: generan la documentación REST Docs (/docs/index.html)
# y la imagen solo se construye si todas pasan.
RUN chmod +x ./mvnw && ./mvnw package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/target/support-ticket-system-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]