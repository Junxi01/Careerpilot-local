FROM gradle:8.10.2-jdk21 AS app
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY src ./src

EXPOSE 8080
ENV BACKEND_PORT=8080

CMD ["gradle", "run", "--no-daemon"]
