# Food Wastage Management System — Backend

Spring Boot 3.3 REST API (Java 17) with Spring Data JPA and MySQL.

Local URL: `http://localhost:8080/api`

## Endpoints used by the React app

| Method | Path | Purpose |
|--------|------|---------|
| GET | `/api/health` | Health + database check |
| GET | `/api/food` | List records (`q`, `category`, `start`, `end`) |
| GET | `/api/food/{id}` | One record |
| POST | `/api/food` | Create |
| PUT | `/api/food/{id}` | Update |
| DELETE | `/api/food/{id}` | Delete |
| GET | `/api/categories` | Distinct categories |
| GET | `/api/summary` | Dashboard / report totals |
| GET | `/api/predict?name=` | Suggested preparation |

## Local MySQL

1. Install MySQL 8 and make sure the service is running (`MySQL80` on Windows).
2. Copy `.env.example` to `.env` and set `DB_USERNAME` / `DB_PASSWORD`.
3. Optional: create the schema yourself:

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/sample_data.sql
```

If you skip the SQL files, Hibernate (`ddl-auto=update`) creates the `food_wastage` database and `food` table on first start, as long as the user can create databases.

Do **not** commit `.env`. Production must use hosting environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `CORS_ORIGINS`, `PORT`.

`DB_URL` format:

```
jdbc:mysql://HOST:3306/food_wastage?sslMode=REQUIRED&serverTimezone=UTC
```

## Run

```bash
mvn spring-boot:run
```

Or `.\mvnw.cmd spring-boot:run` on Windows.

Health check: `http://localhost:8080/api/health`

## Deploy (Render)

1. Create a managed MySQL database (Render, Railway, Aiven, or similar).
2. Import `database/schema.sql` (and optionally `database/sample_data.sql`).
3. Create a **Web Service** from this repository, Docker runtime.
4. Set environment variables:

- `DB_URL` — JDBC URL of the managed MySQL instance
- `DB_USERNAME`
- `DB_PASSWORD`
- `CORS_ORIGINS` — your Vercel/Netlify frontend origin, e.g. `https://food-wastage-frontend.vercel.app`
- `CORS_ORIGIN_PATTERNS` — optional, e.g. `https://*.vercel.app`

The host assigns `PORT`. The app reads it automatically.

Health check path: `/api/health`
