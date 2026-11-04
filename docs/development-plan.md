<!-- Author: Phan Tuan Thanh - CE190595 -->

# Development plan

Branch: `feature/a3-hotel`. Remote: `https://github.com/TuanThanh-dev19/A3PhanTuanThanh_SE1910.git`. The remote had no commits before A3-01; the local Spring scaffold was preserved and extended. Dates use Asia/Bangkok.

| Stage | Work and completion criteria | Status | Completed | Commit |
| --- | --- | --- | --- | --- |
| A3-01 | Spring Boot 3/SQL Server + React/Axios, five entities/composite key, A3 SQL/migration/seed, safe local config, seven docs; actual backend/SQL queries and frontend startup verified | IN PROGRESS | - | Acceptance checks passed; awaiting stage commit/push |
| A3-02 | JWT customer registration/login, configured staff login, BCrypt, role security, public rooms, own profile; Postman success/failure/role checks | TODO | - | - |
| A3-03 | Staff customer/room CRUD/search, room types, protect booking history on deletion; Postman CRUD/validation/authorization | TODO | - | - |
| A3-04 | Transactional one/many-room bookings, unique IDs, server totals, overlap/date/status checks, own history, staff reservations/details; Postman valid/invalid/ownership checks | TODO | - | - |
| A3-05 | React public/auth/staff screens, Axios JWT and guards, real CRUD/search/details and session behavior; staff UI/API verification | TODO | - | - |
| A3-06 | Customer profile, room selection, multi-room booking, history/logout; register-to-history end-to-end with staff visibility | TODO | - | - |
| A3-07 | Postman regression, FE/BE/permissions/history preservation, builds, README/docs consistency, secret review and clean tree | TODO | - | - |

Required stage commit messages:

1. `feat(a3-01): initialize projects and database`
2. `feat(a3-02): add jwt authentication and profile api`
3. `feat(a3-03): implement staff management apis`
4. `feat(a3-04): implement booking apis`
5. `feat(a3-05): build public and staff react pages`
6. `feat(a3-06): build customer booking workflow`
7. `docs(a3-07): document and verify assignment`

Each stage requires functional checks, relevant tests/builds, actual evidence, documentation and a verified remote push before the next stage. The current stage SHA will be recorded in a small follow-up documentation commit, as allowed by the supplied plan; no SHA is invented or history rewritten.
