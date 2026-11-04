<!-- Author: Phan Tuan Thanh - CE190595 -->

# Development plan

Branch: `feature/a3-hotel`. Remote: `https://github.com/TuanThanh-dev19/A3PhanTuanThanh_SE1910.git`. The remote had no commits before A3-01; the local Spring scaffold was preserved and extended. Dates use Asia/Bangkok.

| Stage | Work and completion criteria | Status | Completed | Commit |
| --- | --- | --- | --- | --- |
| A3-01 | Spring Boot 3/SQL Server + React/Axios, five entities/composite key, A3 SQL/migration/seed, safe local config, seven docs; actual backend/SQL queries and frontend startup verified | DONE | 2026-11-04 | `8742d60d8c3689105bcd1c1061396d8f9e996fa5` (remote verified) |
| A3-02 | JWT customer registration/login, configured staff login, BCrypt, role security, public rooms, own profile; Postman success/failure/role checks | DONE | 2026-11-04 | `b8101dee76767187a9f5e7532382ed9f350ac407` (pushed/verified) |
| A3-03 | Staff customer/room CRUD/search, room types, protect booking history on deletion; Postman CRUD/validation/authorization | DONE | 2026-11-04 | `379be0ae5e395e69d0bdf7579f90353c37d84d70` (pushed/verified) |
| A3-04 | Transactional one/many-room bookings, unique IDs, server totals, overlap/date/status checks, own history, staff reservations/details; Postman valid/invalid/ownership checks | DONE | 2026-11-04 | `2d255e2d8dbcb9ff313c05c2a3bef8cf0a87fa5a` (pushed/verified) |
| A3-05 | React public/auth/staff screens, Axios JWT and guards, real CRUD/search/details and session behavior; staff UI/API verification | DONE | 2026-11-04 | `7692bb03b4e4b2fc9f48b500a47dd11b10d7a0fc` (pushed/verified) |
| A3-06 | Customer profile, room selection, multi-room booking, history/logout; register-to-history end-to-end with staff visibility | DONE | 2026-11-04 | `1ff22d48731dad0d1e8c0df44b007ef830e8917c` (pushed/verified) |
| A3-07 | Postman regression, FE/BE/permissions/history preservation, builds, README/docs consistency, secret review and clean tree | DONE | 2026-11-04 | `6f11ca052f3775dd8e361def7cae7aabfe31ac6a` (pushed/verified) |

Required stage commit messages:

1. `feat(a3-01): initialize projects and database`
2. `feat(a3-02): add jwt authentication and profile api`
3. `feat(a3-03): implement staff management apis`
4. `feat(a3-04): implement booking apis`
5. `feat(a3-05): build public and staff react pages`
6. `feat(a3-06): build customer booking workflow`
7. `docs(a3-07): document and verify assignment`

Each stage requires functional checks, relevant tests/builds, actual evidence, documentation and a verified remote push before the next stage. This follow-up documentation commit records the actual A3-01 implementation SHA after its successful push, as allowed by the supplied plan. No history was rewritten.

A3-01 closure: seven SQL Server integration tests passed, React production build passed, Postman executed two requests with six passing assertions, direct/proxied API data matched, and the browser rendered the three seeded rooms plus the offline error state. The original-derived SQL path and repeatable migration/seed were also tested in an isolated database. See [test matrix](test-matrix.md) for evidence. A3-01 remains verified; subsequent stage completion is recorded above.

A3-07 verification: 29 backend tests, 3 frontend date tests, 57 Postman requests/74 assertions, format/production build, current SQL migration/seed checks and final browser checks passed. Author/credential/link audit passed. The implementation was pushed, its remote SHA matched, and its working tree was clean. This small documentation commit records that actual SHA as allowed by the supplied plan; no history was rewritten. All seven stages are complete. See [test matrix](test-matrix.md) and [Git evidence](evidence/a3-07-git-closure.txt).
