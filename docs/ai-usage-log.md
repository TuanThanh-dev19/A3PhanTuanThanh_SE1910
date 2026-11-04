<!-- Author: Phan Tuan Thanh - CE190595 -->

# AI usage log

| Date/stage | Tool | Generated/proposed work | Actual verification |
| --- | --- | --- | --- |
| 2026-11-04 / A3-01 | OpenAI Codex | Read PDF, SQL and plan; extend scaffold; JPA/schema/seed, catalog, React/Axios and initial docs | JPA startup, SQL-path checks, 7 real SQL Server tests, frontend build, 2 Postman requests/6 assertions and browser catalog/error checks |
| 2026-11-04 / A3-02 | OpenAI Codex | BCrypt, configured staff login, JWT/roles, own profile, DTO validation and auth tests | 14 backend tests and 18 Postman assertions passed |
| 2026-11-04 / A3-03 | OpenAI Codex | Staff customer/room CRUD/search, validation and history-safe deletion | 18 backend tests and 34 Postman assertions passed |
| 2026-11-04 / A3-04 | OpenAI Codex | Transactional booking/details, server totals, overlap/ownership, cancellation and staff management | 25 backend tests and 68 Postman assertions passed |
| 2026-11-04 / A3-05 | OpenAI Codex | Public/auth/staff React routes, Axios session handling, forms, search/details and browser fixes | Frontend build, 25 backend regressions and actual staff login/create/search/edit/booking/cancel browser actions passed |
| 2026-11-04 / A3-06 | OpenAI Codex | Customer profile, multi-room booking, own history and logout | Frontend build and real register-to-history flow passed; staff viewed the same booking |
| 2026-11-04 / A3-07 | OpenAI Codex | Concurrency/atomic-edit/UTF-8 password/uniqueness/date regressions, readable formatting, stale-search protection, final docs/evidence; preserve and finish interrupted work | Inspected workspace/source/logs; reran 29 SQL Server backend tests, 3 frontend tests, format/build and 57 Postman requests/74 assertions; completed browser 401/restoration/history/staff checks |

Verification was performed by the agent through code inspection, actual commands and real browser interaction. No human review is claimed. Evidence contains no real passwords, signing secrets or JWTs. Local credentials remained in ignored configuration/runtime variables. JSON and untouched generated wrappers were not changed just to insert author comments. No mock backend/data replaced the real application workflow.
