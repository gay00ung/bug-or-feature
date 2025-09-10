# Build and run standalone Ktor API server for Railway

FROM eclipse-temurin:17-jdk as build
WORKDIR /app

COPY . .
RUN ./gradlew :site:installDist --no-daemon

FROM eclipse-temurin:17-jre
ENV PORT=8080
WORKDIR /app

# Copy installed distribution
COPY --from=build /app/site/build/install/site /app/site

EXPOSE 8080
CMD ["/app/site/bin/site"]

