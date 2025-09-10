# Build and run standalone Ktor API server for Railway

FROM eclipse-temurin:17-jdk as build
WORKDIR /app

COPY . .
RUN ./gradlew :site:serverFatJar --no-daemon

FROM eclipse-temurin:17-jre
ENV PORT=8080
WORKDIR /app

COPY --from=build /app/site/build/libs/site-server.jar /app/app.jar

EXPOSE 8080
CMD ["java","-jar","/app/app.jar"]
