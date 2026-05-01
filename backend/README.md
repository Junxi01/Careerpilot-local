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
curl -s http://localhost:8080/health/db
```

### Auth endpoints (Day 6)

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/me` (requires `Authorization: Bearer <token>`)

Example:

```bash
curl -s http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"you@example.com","password":"password123","displayName":"You"}'
```

### Database connectivity (Day 5)

This backend uses:

- **HikariCP** connection pool
- **MySQL JDBC driver**

Schema management decision:

- **Direct `database/schema.sql` compatibility**: schema is applied externally (e.g. Docker MySQL init scripts).
- The backend **does not run migrations yet** (no Flyway at this stage).

The server reads DB config from environment variables:

- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`

### Manual DB verification (with Docker Compose MySQL)

Start MySQL first (from repo root):

```bash
cd ..
cp .env.example .env
docker compose up -d mysql
```

Run the backend (this directory):

```bash
cd backend
# IMPORTANT:
# - If backend runs on your host machine: DB_HOST should be "localhost"
# - If backend runs as a Docker service on the same compose network: DB_HOST should be "mysql"
DB_HOST=localhost DB_PORT=3306 DB_NAME=careerpilot DB_USER=careerpilot DB_PASSWORD=careerpilot_password ./gradlew run
```

Then:

```bash
curl -i http://localhost:8080/health/db
```

