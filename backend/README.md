## Backend (Kotlin + Ktor)

### Requirements

- JDK 21
- Gradle (or use the Gradle Docker image)

### Run tests

Local Gradle:

```bash
cd backend
gradle test
```

Via Docker (no local Gradle needed):

```bash
cd backend
docker run --rm -v "$PWD":/app -w /app gradle:8.10.2-jdk21 gradle test --no-daemon
```

### Run the server

```bash
cd backend
gradle run
```

By default it listens on port **8080**.

### Manual verification

```bash
curl -s http://localhost:8080/health
curl -s http://localhost:8080/api/version
```

### Environment variables (read-only for now)

The server currently **reads** (but does not connect to MySQL yet):

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

