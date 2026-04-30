## Database

SQL schema and seed files live here.

- `schema.sql`: table definitions (initial baseline). Docker Compose mounts this file to MySQL’s `/docker-entrypoint-initdb.d/` on **first** container init (empty data volume). **`MYSQL_DATABASE` / `DB_NAME` must match** the database name in `schema.sql` (`USE \`careerpilot\`;` by default).
- `seed.sql`: optional local seed data

Migrations will be added later once the domain model stabilizes.
