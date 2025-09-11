# Build and run standalone Ktor API server for Railway

FROM eclipse-temurin:17-jdk as build
WORKDIR /app

COPY . .
RUN ./gradlew :site:serverFatJar --no-daemon \
    && (test -f site/build/libs/site-server.jar || (echo "Jar not found. Contents of site/build/libs:" && ls -la site/build/libs && exit 1)) \
    && cp site/build/libs/site-server.jar /app/app.jar

FROM eclipse-temurin:17-jre
ENV PORT=8080
WORKDIR /app

COPY --from=build /app/app.jar /app/app.jar
COPY docker-entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["/app/entrypoint.sh"]
