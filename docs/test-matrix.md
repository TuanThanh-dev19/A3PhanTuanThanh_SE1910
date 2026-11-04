<!-- Author: Phan Tuan Thanh - CE190595 -->

# Test matrix

Executed on 2026-11-04 (Asia/Bangkok). Backend tests use the real SQL Server 2022 assignment database. PASS means the scenario was actually executed. Later stages remain NOT RUN.

| ID | Role | Scenario | Expected | Actual | Status | Evidence |
| --- | --- | --- | --- | --- | --- | --- |
| A01-01 | Developer | JPA bootstrap on empty A3 database | Backend starts, creates five tables and sequence | Bootstrap started; SQL metadata confirmed five tables/sequence | PASS | [Bootstrap/startup](evidence/a3-01-startup.txt), [SQL](evidence/a3-01-database.txt) |
| A01-02 | Developer | Normal startup/schema validation | Connect to SQL Server with ddl-auto=validate | Backend started on 8081 and catalog queries succeeded | PASS | [Startup](evidence/a3-01-startup.txt), [HTTP](evidence/a3-01-http-checks.txt) |
| A01-03 | Developer | Original-derived SQL schema, migration and seed | Five tables; password length 255; sequence; four protected FKs; seed repeatable | Isolated database passed assertions; migration/seed twice retained two types/three rooms; test database removed | PASS | [SQL scripts](evidence/a3-01-sql-scripts.txt) |
| A01-04 | Guest | GET rooms without credentials | SQL-backed room DTOs and room type relationship | HTTP 200, seeded room 101/Standard/500000; no password fields | PASS | [Postman](evidence/a3-01-postman.txt), [backend tests](evidence/a3-01-backend-build.txt) |
| A01-05 | Guest | GET room types without credentials | Seeded Standard and Deluxe | HTTP 200 with both types | PASS | [Postman](evidence/a3-01-postman.txt) |
| A01-06 | Guest | Attempt POST /api/rooms in bootstrap | No mutation endpoint | HTTP 405 in MockMvc integration test | PASS | [Backend tests](evidence/a3-01-backend-build.txt) |
| A01-07 | Developer | Persist password longer than original 50-character limit | 60-character value round-trips, column 255 | Both database assertions succeeded; row rolled back | PASS | [Backend tests](evidence/a3-01-backend-build.txt) |
| A01-08 | Developer | Persist reservation/details and relationships | Distinct sequence IDs, composite lookup, money round-trip | Two IDs differed; detail and customer/room/type relationships loaded | PASS | [Backend tests](evidence/a3-01-backend-build.txt) |
| A01-09 | Developer | Delete room referenced by a test detail | FK rejects deletion; history stays | DataIntegrityViolationException asserted; detail still existed; test rolled back | PASS | [Backend tests](evidence/a3-01-backend-build.txt) |
| A01-10 | Developer | Verify four FK delete rules | No cascading deletes | Four FKs; zero cascading delete actions | PASS | [Backend tests](evidence/a3-01-backend-build.txt), [SQL](evidence/a3-01-database.txt) |
| A01-11 | Guest | React -> Axios -> Vite proxy -> backend -> SQL | Render real data and preserve values | Browser showed rooms 101/102/201; direct/proxied JSON matched | PASS | [React screenshot](evidence/a3-01-react-catalog.jpg), [HTTP](evidence/a3-01-http-checks.txt) |
| A01-12 | Guest | React when A3 backend is offline | Clear English error without fake room data | Browser displayed 'Unable to load rooms. Please try again later.' | PASS | [Error screenshot](evidence/a3-01-api-unavailable.jpg) |
| A01-13 | Developer | Maven verify / React production build | Relevant tests/builds pass | Seven tests, zero failures/errors/skips; Vite production build succeeded | PASS | [Maven](evidence/a3-01-backend-build.txt), [Vite](evidence/a3-01-frontend-build.txt) |
| A01-14 | Developer | Author headers, secrets and ignored outputs | Headers where supported; credentials/build outputs excluded | Headers/JSON/docs links checked; local password and common key/token patterns absent; generated output ignored | PASS | [Source audit](evidence/a3-01-source-audit.txt) |
| A02-01 | Customer | Register/login with valid email/password | BCrypt storage and valid JWT | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-02-postman.txt) |
| A02-02 | Customer | Duplicate email, wrong password, invalid registration | Clear validation/authentication error | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-02-postman.txt) |
| A02-03 | Guest/Customer | Missing/expired/invalid token and wrong role | Protected API rejects request with appropriate status | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-02-postman.txt) |
| A02-04 | Staff | Login using configured staff account | STAFF JWT, no staff table | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-02-postman.txt) |
| A02-05 | Customer | Read/update own profile without password disclosure | Own profile only, valid input, safe DTO | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-02-postman.txt) |
| A03-01 | Staff | Customer and room CRUD/search, room-type selection | Persist/search real records | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-03-postman.txt) |
| A03-02 | Guest/Customer | Attempt staff APIs | Denied | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-03-postman.txt) |
| A03-03 | Staff | Delete unused room versus previously booked room | Hard delete unused room; status change for referenced room | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-03-postman.txt) |
| A03-04 | Staff | Delete customer with history, invalid field values | Preserve history and reject invalid data | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-03-postman.txt) |
| A04-01 | Customer | Create one-room and multi-room booking | One transaction, correct server total and unique ID | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-04-postman.txt) |
| A04-02 | Customer | Missing room, inactive room, invalid dates and overlap | Clear rejection without partial booking | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-04-postman.txt) |
| A04-03 | Customer | Own history versus another customer's booking | Own records only | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-04-postman.txt) |
| A04-04 | Staff | Manage reservations/details with valid and invalid edits | Safe updates and preserved history | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-04-postman.txt) |
| A05-01 | Staff | React login, guarded routes, CRUD/search/details, refresh | Real API workflow and clear errors | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-05-ui.txt) |
| A06-01 | Customer | Register -> login -> profile -> select rooms -> booking -> history -> logout | Real end-to-end workflow; staff sees new booking | Executed successfully; see stage verification below | PASS | [Stage evidence](evidence/a3-06-ui.txt) |
| A07-01 | All | Full Postman/React regression and permissions | All final requirements verified | Only A3-01 subset executed | NOT RUN | - |

A01-09 verifies database history protection, not the later staff soft-delete business rule. Postman A3-01 ran two GET requests with six passing assertions; auth/CRUD/booking Postman checks belong to later stages. Evidence text contains actual concise command output/excerpts, not reconstructed test results. Screenshots capture the browser's real rendered page.

## A3-02 verification

JWT authentication, BCrypt registration, configured staff login, role authorization and own profile APIs.

Backend: [actual Maven result](evidence/a3-02-backend-build.txt). API: [actual Postman result](evidence/a3-02-postman.txt). Source: [author/credential checks](evidence/a3-02-source-audit.txt).

## A3-03 verification

Staff customer/room CRUD and search, room-type lookup, referenced-room deactivation and customer-history protection.

Backend: [actual Maven result](evidence/a3-03-backend-build.txt). API: [actual Postman result](evidence/a3-03-postman.txt). Source: [author/credential checks](evidence/a3-03-source-audit.txt).

## A3-04 verification

Transactional multi-room bookings, ownership, availability/date checks, server totals and staff reservation/detail management.

Backend: [actual Maven result](evidence/a3-04-backend-build.txt). API: [actual Postman result](evidence/a3-04-postman.txt). Source: [author/credential checks](evidence/a3-04-source-audit.txt).

## A3-05 verification

React public/authentication pages and guarded staff customer, room and booking screens with real Axios requests.

Backend: [actual Maven result](evidence/a3-05-backend-build.txt). Source: [author/credential checks](evidence/a3-05-source-audit.txt).

## A3-06 verification

React customer profile, room selection, multi-room booking, own history and logout workflow.

Source: [author/credential checks](evidence/a3-06-source-audit.txt).
