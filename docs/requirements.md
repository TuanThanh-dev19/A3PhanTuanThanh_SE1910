<!-- Author: Phan Tuan Thanh - CE190595 -->

# Requirements

## Assignment requirements

Sources reviewed: all six pages of the supplied Assignment 03 PDF, its database diagram, the supplied Assignment 02 SQL, and the user's seven-stage Markdown plan. The PDF's isolated references to vehicles/news are inconsistent with the hotel schema and main functions; hotel requirements govern this implementation.

Build FUMiniHotelSystem with Spring Boot **3**, SQL Server, Spring Data JPA, a Controller -> Service -> Repository architecture, React and Axios. Secure later stages with Spring Security/JWT. Validate inputs, exercise APIs through Postman and test real frontend/backend interactions.

Database, Spring project and React project: `A3PhanTuanThanh_SE1910`. Author: Phan Tuan Thanh - CE190595. All project UI, errors, comments and documentation are English.

| Role | Required access | A3-01 state |
| --- | --- | --- |
| Guest | View room information without authentication | Read-only catalog preview implemented |
| Customer | Register, email/password JWT login, own profile, create one/many-room booking, own booking history | Authentication/profile APIs implemented; Booking/profile/authentication APIs implemented; customer UI planned A3-06 |
| Staff | Login from application configuration; manage customers, rooms, reservations and details; CRUD/search | Configured login/customer-room APIs implemented; Booking/customer-room APIs implemented; staff screens implemented A3-05 |

A room may be hard-deleted only if no booking detail references it. Otherwise change its status and preserve history. A room belongs to one room type; a customer has many reservations; each reservation has one or more details, with one room per detail. `BookingDetail` has composite PK `(BookingReservationID, RoomID)`.

## Plan requirements and implementation decisions

| Item | Basis | Decision/current state |
| --- | --- | --- |
| Five SQL/JPA entities | Assignment/schema | All mapped, including four lazy many-to-one relationships and composite key |
| SQL bootstrap and seed | User plan | Original SQL preserved externally; separate original-derived A3 schema, migration, seed |
| Password storage length | Plan, BCrypt compatibility | `nvarchar(255)`; BCrypt hashing implemented in A3-02 |
| Reservation IDs | Plan, safe allocation | SQL Server sequence, increment/allocation 1 |
| Delete cascades | Plan, history protection | Four FKs use `NO ACTION`; no JPA remove cascades |
| Schema generation | Assignment JPA objective | Explicit bootstrap profile creates tables; normal startup validates |
| Currency/status | Implementation decision | Seed prices displayed as VND; room status 1 = active, 2 = inactive (future writes) |
| Overlap definition | Not specified by assignment | Half-open date ranges `[start, end)` and transactional validation implemented in A3-04 |
| Historical customer deletion | Plan | Service rejects customers with booking history; FK also protects records |
| Development networking | Local decision | Loopback backend 8081; Vite 5173 with `/api` proxy |
| Secrets | User requirement | Environment/ignored local properties, placeholders only in Git |

Deliver exactly one stage at a time with real checks, updated docs/evidence, English commit and a normal GitHub push. Never mark unexecuted cases PASS. A3-02 authentication/profile is implemented; A3-03 staff customer/room APIs are implemented; A3-04 booking APIs are implemented; A3-05 public/auth/staff screens are implemented; A3-06/A3-07 remain pending.
