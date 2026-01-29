# Crossfit Backend

## Run (local)
1) Start MySQL:
   - `docker compose up db`
2) Run API:
   - `./gradlew bootRun`

## Env
- DB: `jdbc:mysql://localhost:3306/crossfit` (user/pass `crossfit`)
- JWT secret: `app.jwt.secret` in `application.yml`

## Notes
- Flyway migrations in `src/main/resources/db/migration`
- Default timeslots are auto-created on first `GET /sessions?date=YYYY-MM-DD`
