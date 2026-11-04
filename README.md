<!-- Author: Phan Tuan Thanh - CE190595 -->

# FUMiniHotelSystem

SBA301 Assignment 03 by Phan Tuan Thanh (CE190595). Project/database name: `A3PhanTuanThanh_SE1910`, confirmed by the supplied repository and existing Spring project. The class code is SE1910.

A3-02 includes JWT customer/staff authentication and customer profile APIs, alongside the SQL Server/JPA foundation and React catalog preview. Staff/booking APIs and staff React screens are implemented. Customer profile, multi-room booking and own history screens are also implemented.

## Requirements

- Java 21 and Maven 3.6.3+ (or the supplied Maven wrapper).
- SQL Server 2022, TCP enabled, and SQL Server command-line tools or SSMS.
- Node.js 24 LTS and npm. Run commands from the repository root unless stated otherwise.
- Backend: Spring Boot 3.5.16, Spring Web, Data JPA, Validation and Microsoft JDBC driver.
- Frontend: React 19, Axios and Vite 8 in `frontend/A3PhanTuanThanh_SE1910`.

The React directory and page title use the required project name. Its npm package identifier is lowercase (`a3phantuanthanh_se1910`) to follow npm naming rules.

## Local configuration

Copy the variable names from `.env.example` into your process environment, replacing every placeholder. Spring does not load `.env` automatically. Alternatively, create the ignored root file `application-local.properties`:

```properties
# Author: Phan Tuan Thanh - CE190595
spring.datasource.username=replace_with_local_database_user
spring.datasource.password=replace_with_local_database_password
```

The default connection targets `localhost:1433`, database `A3PhanTuanThanh_SE1910`. Override `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `SERVER_ADDRESS` or `SERVER_PORT` as needed. The local SQL Server certificate is trusted by the development URL; use a trusted server certificate and `trustServerCertificate=false` outside local development.

Provision a SQL login mapped only to this database. Schema creation/migration requires DDL permissions; normal execution needs read/write permissions. No database password, JWT secret or staff password is included in Git. STAFF_EMAIL, STAFF_PASSWORD and JWT_SECRET are required. Use at least 32 random bytes for JWT_SECRET and a staff password of 8 to 72 UTF-8 bytes. In ignored local properties, use app.staff.email, app.staff.password and app.jwt.secret.

## Database setup

Choose one schema creation path on an empty assignment database. Neither path changes the supplied Assignment 02 file or other databases.

**JPA path (verified on this machine):**

1. Run `database/01-create-database.sql` using an administrator connection. Configure the application's database login.
2. Run `mvn -DskipTests package`, then `java -jar target/A3PhanTuanThanh_SE1910-0.0.1-SNAPSHOT.jar --spring.profiles.active=bootstrap`. Hibernate creates all five tables and the sequence. Stop this process after startup.
3. Run `database/03-a3-migration.sql`, then `database/04-seed.sql` against SQL Server.
4. Start normally without the bootstrap profile; Hibernate validates the schema.

**SQL path (also verified in an isolated database):** run `01-create-database.sql`, `02-original-schema.sql`, `03-a3-migration.sql` and `04-seed.sql` in order. The original-derived schema must run only once on an empty database. Do not run it after JPA has created tables. Migration and seed can be repeated. They are manual scripts, not Flyway migrations.

Example for the local named instance with Windows authentication:

```powershell
sqlcmd -S '.\SQL2022' -E -C -b -i database/01-create-database.sql
# Choose JPA bootstrap above, or run database/02-original-schema.sql here.
sqlcmd -S '.\SQL2022' -E -C -b -i database/03-a3-migration.sql
sqlcmd -S '.\SQL2022' -E -C -b -i database/04-seed.sql
```

Migration widens `Customer.Password` to `nvarchar(255)` and replaces four cascading FKs with `NO ACTION`. Booking IDs use `BookingReservationSequence`, never `MAX(id)+1`. If the original schema already has bookings but no sequence, migration stops for a reviewed starting value. Seed data contains only two room types and three rooms; no customer credentials are seeded.

## Run and verify

```powershell
mvn clean verify
java -jar target/A3PhanTuanThanh_SE1910-0.0.1-SNAPSHOT.jar
```

In a second terminal:

```powershell
npm ci --prefix frontend/A3PhanTuanThanh_SE1910
npm run dev --prefix frontend/A3PhanTuanThanh_SE1910
```

Open `http://127.0.0.1:5173`. Backend: `http://127.0.0.1:8081`. Port 8081 avoids an existing local application on 8080. Vite forwards `/api` to 8081; update `vite.config.js` if you change the backend port. Vite preview only serves build artifacts and does not provide this development API proxy.

```powershell
npm run build --prefix frontend/A3PhanTuanThanh_SE1910
./scripts/verify-a3-01.ps1
postman collection run postman/a3-01-bootstrap.postman_collection.json --reporters cli
```

Seven integration tests use the actual configured SQL Server, not H2. Test writes roll back; identity/sequence counters may still advance. Stop the packaged backend before `mvn clean verify` on Windows because the running JAR is locked. Authentication configuration is required at startup; public GET requests need no token. Use scripts/start-backend.ps1 after a successful build to run an ignored copy of the JAR and avoid Windows build locks.

## Implemented endpoints

| Method | Route | Result |
| --- | --- | --- |
| GET | `/api/rooms` | Public room DTOs including room-type name and nightly price |
| GET | `/api/room-types` | Public room-type DTOs for bootstrap verification |

A3-02 adds public POST /api/auth/register and /api/auth/login, and customer-only GET/PUT /api/profile. Login returns token, role, name and expiresIn. Send Authorization: Bearer <token> to protected APIs. React includes public rooms, login/register, guarded staff customer/room/booking pages and customer profile/book/history pages. Session storage preserves the one-hour JWT across refreshes within the tab.

## Documentation

- [Requirements](docs/requirements.md) and [seven-stage plan](docs/development-plan.md)
- [Design](docs/design.md) and [code flow](docs/code-flow.md)
- [Test matrix](docs/test-matrix.md), [debug log](docs/debug-log.md), [AI usage](docs/ai-usage-log.md)
- [Actual evidence](docs/evidence/)
