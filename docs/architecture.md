## Architecture notes (initial)

### Goals
- Local-first, self-hosted workflow
- No hardcoded secrets (use `.env`)
- Typed, consistent backend API responses
- Frontend uses TypeScript types for all API responses
- AI features support real-provider mode and mock mode for local testing
- Only support user-configured **public** company career pages (no login-required sites)

### Planned high-level components
- **Backend (Kotlin + Ktor)**: REST API, scheduling, integrations
- **Frontend (React + TS + Vite)**: dashboard + workflows
- **Database (MySQL)**: durable storage for leads, applications, reminders, reports, backups metadata
- **Scripts (Python)**: automation helpers (fetch, parse, backups), always safe + dry-run
