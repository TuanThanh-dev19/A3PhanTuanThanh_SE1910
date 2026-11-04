<!-- Author: Phan Tuan Thanh - CE190595 -->

# FUMiniHotelSystem

SBA301 Assignment 03 by **Phan Tuan Thanh - CE190595**, class SE1910. Database and Spring/React project name: `A3PhanTuanThanh_SE1910`. The existing root Maven project was retained; React is in `frontend/A3PhanTuanThanh_SE1910`. Its npm identifier is lowercase to satisfy npm naming rules.

Implemented: public room browsing; customer registration, JWT login, profile, multi-room booking and own history; configured staff login and customer/room/reservation/detail management with search. Spring Boot 3 uses Controller -> Service -> Repository, Spring Data JPA and SQL Server. React calls real APIs through Axios.

## Requirements and local settings

Use Java 21, Maven 3.6.3+ (or `./mvnw.cmd`), SQL Server 2022 with TCP enabled, Node.js 24 and npm. Postman CLI is needed for the API checks. Run commands from the repository root.

Copy variable names from `.env.example` into your process environment and replace **every** placeholder. Spring does not automatically load `.env`. Alternatively, create this ignored root `application-local.properties`:

```properties
# Author: Phan Tuan Thanh - CE190595
spring.datasource.username=replace_with_local_database_user
spring.datasource.password=replace_with_local_database_password
app.staff.email=replace_with_staff_email
app.staff.password=replace_with_staff_password
app.jwt.secret=replace_with_random_secret_of_at_least_32_bytes
```

Use one configuration approach; an existing local properties file can override the environment placeholders in the base configuration. Staff email/password must be configured; staff is not stored in a database table. Staff passwords require at least 8 characters and at most 72 UTF-8 bytes. Generate a random JWT secret with at least 32 bytes. No real database/staff password, signing key or JWT is supplied in Git.

| Variable | Purpose/default |
| --- | --- |
| `DB_URL` | SQL Server JDBC URL; defaults to localhost:1433 and `A3PhanTuanThanh_SE1910` |
| `DB_USERNAME`, `DB_PASSWORD` | Application database account |
| `STAFF_EMAIL`, `STAFF_PASSWORD` | Configured staff login |
| `JWT_SECRET` | HS256 signing secret; at least 32 bytes |
| `SERVER_ADDRESS`, `SERVER_PORT` | Loopback `127.0.0.1`, port `8081` |

The development JDBC URL encrypts traffic and trusts the local SQL Server certificate. For deployment, supply a trusted certificate and `trustServerCertificate=false`. Map the database login only to the assignment database; bootstrap/migration requires DDL permissions, while ordinary application use requires read/write permissions.

## Database preparation

Run `database/01-create-database.sql` as an administrator, then configure the database login. Choose **one** schema creation path on an empty A3 database:

1. **JPA path:** run `mvn -DskipTests package`, then `java -jar target/A3PhanTuanThanh_SE1910-0.0.1-SNAPSHOT.jar --spring.profiles.active=bootstrap`. Hibernate creates five mapped tables and the reservation sequence. Stop this bootstrap process, then run scripts `03-a3-migration.sql` and `04-seed.sql`.
2. **SQL path:** run `02-original-schema.sql`, `03-a3-migration.sql` and `04-seed.sql` in order. Do not run script 02 after JPA has created tables.

Example using the local named instance and Windows authentication:

```powershell
sqlcmd -S '.\SQL2022' -E -C -b -i database/01-create-database.sql
# Choose JPA bootstrap above, or run database/02-original-schema.sql here.
sqlcmd -S '.\SQL2022' -E -C -b -i database/03-a3-migration.sql
sqlcmd -S '.\SQL2022' -E -C -b -i database/04-seed.sql
```

The supplied Assignment 02 file remains unchanged. Script 02 adapts its schema to the A3 database. Script 03 widens passwords to `nvarchar(255)`, provides a SQL Server sequence, replaces four cascading foreign keys with `NO ACTION`, and enforces unique room numbers. It can be rerun, including on an existing A3-01 database. Existing bookings without a sequence require a reviewed sequence start; migration stops rather than guessing an ID. Script 04 can be rerun and seeds Standard/Deluxe plus rooms 101/102/201, with no credentials.

These are manual scripts, not Flyway migrations. Normal startup uses `ddl-auto=validate`; only the explicit bootstrap profile uses `update`. JPA bootstrap was exercised in A3-01; final schema validation and the current original-derived SQL/migration/seed path were verified in A3-07.

## Run

```powershell
mvn clean verify
./scripts/start-backend.ps1
```

The helper starts an ignored copy of the packaged JAR, avoiding Windows locks during subsequent Maven builds. Logs are in `tmp/runtime/`; wait for the Tomcat startup line. Stop it with `./scripts/start-backend.ps1 -Stop`. Alternatively, run the packaged JAR directly, stopping it before rebuilding on Windows.

In a second terminal:

```powershell
npm ci --prefix frontend/A3PhanTuanThanh_SE1910
npm run dev --prefix frontend/A3PhanTuanThanh_SE1910
```

Open [the application](http://127.0.0.1:5173/). Backend: [public rooms](http://127.0.0.1:8081/api/rooms). Port 8081 avoids another local application on 8080. Vite forwards `/api` to 8081; change its proxy if you change the backend port. `vite preview` serves build artifacts without this development proxy.

## APIs and screens

All routes below start with `/api`. Login returns `token`, `role`, `name`, `expiresIn` (3600 seconds). Protected requests use `Authorization: Bearer <token>`.

| Access | Method and route | Behavior |
| --- | --- | --- |
| Public | GET `/rooms`, `/room-types` | Room and type DTOs |
| Public | POST `/auth/register`, `/auth/login` | Customer registration; customer/configured-staff JWT login |
| Customer | GET/PUT `/profile` | Own profile; no password/hash response |
| Customer | GET/POST `/bookings`, GET `/bookings/{id}` | Own history, create a reservation, view an owned item |
| Staff | GET/POST `/staff/customers`, GET/PUT/DELETE `/staff/customers/{id}` | CRUD/search; reject deletion with history |
| Staff | GET/POST `/staff/rooms`, GET/PUT/DELETE `/staff/rooms/{id}` | CRUD/search; delete unused rooms or deactivate referenced rooms |
| Staff | GET/POST `/staff/bookings`, GET/PUT/DELETE `/staff/bookings/{id}` | Search/create/view/edit details; DELETE cancels and retains history |
| Staff | PATCH `/staff/bookings/{id}/status` | Cancel/reactivate, with availability checks on reactivation |

Staff lists accept `?search=`: customer name/email, room number/description, or booking ID/customer name/email. Missing authentication returns 401, wrong role 403, missing resources 404, validation 400, and conflicts 409.

Registration uses `{profile: {customerFullName, telephone, emailAddress, customerBirthday}, password}`; login uses `{email, password}`. Customer booking uses `{rooms: [{roomId, startDate, endDate}]}`. Staff booking also requires `customerId` and `bookingStatus`. See [design](docs/design.md) for validation/status rules and [the final Postman collection](postman/a3-07-regression.postman_collection.json) for runnable requests.

React routes: `/`, `/login`, `/register`; staff `/staff/customers`, `/staff/rooms`, `/staff/bookings`; customer `/profile`, `/book`, `/history`. JWTs are kept in sessionStorage for the tab and expire after one hour. Refresh preserves the session; sign-out clears it; a protected API 401 clears it and returns to login. Passwords are not stored in browser storage.

## Verification and demo

```powershell
mvn clean verify
npm test --prefix frontend/A3PhanTuanThanh_SE1910
npm run format:check --prefix frontend/A3PhanTuanThanh_SE1910
npm run build --prefix frontend/A3PhanTuanThanh_SE1910
./scripts/run-postman.ps1 -Collection postman/a3-07-regression.postman_collection.json
```

Actual final results: **29 backend tests**, **3 frontend date tests**, **57 Postman requests / 74 assertions**, formatting and production build passed. Backend tests use the configured SQL Server, not H2. Transactional fixtures roll back; the concurrency test cleans only its dedicated records. Identity/sequence values may advance. Postman and browser checks retain synthetic cancelled/history records and inactive test rooms so history preservation remains reviewable.

Demo: browse rooms as Guest -> register -> sign in -> update profile -> select two rooms/dates -> save -> inspect own history -> sign out. Sign in with your configured staff credentials -> search the customer -> inspect/edit the booking -> cancel/reactivate -> manage customers/rooms. Deleting a referenced room changes its status, retaining booking details. The recorded browser booking is #39, rooms 101/102, total VND 2,100,000; no test password is published.

[Requirements](docs/requirements.md) | [Development plan and commits](docs/development-plan.md) | [Design](docs/design.md) | [Code flow](docs/code-flow.md) | [Test matrix](docs/test-matrix.md) | [Debug log](docs/debug-log.md) | [AI usage](docs/ai-usage-log.md) | [Evidence](docs/evidence/)
