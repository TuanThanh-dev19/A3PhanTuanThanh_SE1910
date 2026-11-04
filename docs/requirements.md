<!-- Author: Phan Tuan Thanh - CE190595 -->

# Requirements

Sources reviewed: all six pages of Assignment 03 (including its schema diagram), the supplied Assignment 02 SQL and the user's seven-stage plan. The PDF's isolated vehicles/news wording conflicts with its hotel schema and main functions; the hotel requirements govern this project.

## Assignment requirements and implementation

| Requirement | Implemented behavior |
| --- | --- |
| Spring Boot 3, SQL Server, JPA and three layers | Boot 3.5.16; Controller -> Service -> Repository; five mapped entities |
| Required project/database name | A3PhanTuanThanh_SE1910; root Spring project and named React directory |
| Public room information | Guest GET rooms and React catalog without authentication |
| Customer account and JWT | Registration, email/password login, BCrypt storage, one-hour signed JWT |
| Staff account from application properties | Environment/ignored application-local.properties; no Staff table |
| Customer profile and history | Own profile GET/PUT, own reservation list/item, no password DTOs |
| Online multi-room booking | One transaction, one or many room details, server-calculated total |
| Staff management/CRUD/search | Customer, room and booking APIs/screens including reservation details |
| Room deletion rule | Unreferenced room deleted; referenced room deactivated; history remains |
| React/Axios, validation and testing | Role routes, real REST calls, DTO/client checks, Postman and browser verification |

| Role | Allowed scope |
| --- | --- |
| Guest | Browse rooms/types, register and sign in |
| Customer | Public catalog plus own profile, online booking and own history |
| Staff | Public catalog plus customer/room/reservation/detail management and search |

Customer ID is derived from JWT for customer operations; another customer's booking returns 404. Staff cannot use customer-only routes. The model is RoomType -> RoomInformation, Customer -> BookingReservation, and BookingDetail joining reservations/rooms with composite PK (BookingReservationID, RoomID).

## Plan requirements and implementation decisions

- Required by plan: Java 21, separate A3 SQL scripts, password width 255, SQL Server sequence (never MAX+1), seed data, no cascade history loss, seven English docs and real evidence, staged normal Git pushes.
- Status decisions: customer/room 1 active and 2 inactive; booking 1 active and 2 cancelled. DELETE reservation means cancellation; referenced customer deletion returns 409 so staff can deactivate instead.
- Booking decisions: 1-20 distinct rooms; dates start today or later, checkout follows check-in, year at most 9999; half-open [start,end) permits adjacent stays. Cancelled bookings do not block dates. Room locks serialize overlap checks; BigDecimal calculates nightly price times nights. Client totals are ignored.
- Room decisions: unique room number, positive capacity and nightly price; price at most 1,000,000,000 with up to four decimal places. Currency is VND. These limits/status meanings are implementation choices rather than exact PDF wording.
- JPA bootstrap explicitly creates tables; ordinary startup validates. SQL migration/seed are manual and repeatable. Four FKs use NO ACTION and JPA relationships have no remove cascade.
- All created/modified source files have Author: Phan Tuan Thanh - CE190595 where comments are supported. JSON and untouched generated wrappers follow the user's exceptions. Credentials and generated output are ignored.

A3-01 through A3-06 were completed sequentially and pushed before later implementation. Final verification and Git closure are recorded in [development-plan.md](development-plan.md) and [test-matrix.md](test-matrix.md).
