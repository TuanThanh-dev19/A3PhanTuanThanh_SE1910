<!-- Author: Phan Tuan Thanh - CE190595 -->

# AI usage log

| Date/stage | Tool | Requested assistance and generated work | Verification and outcome |
| --- | --- | --- | --- |
| 2026-11-04 / A3-01 | OpenAI Codex | Read all three supplied inputs; inspect existing scaffold/remote; create Spring Boot 3/JPA entities, composite key/repositories, read-only catalog DTO/service/controller, SQL scripts, React/Axios preview, integration checks and English docs | Agent inspected source and actual SQL metadata; executed JPA bootstrap, SQL migration/seed and isolated original-derived schema checks; seven real SQL Server integration tests passed; React production build passed; Postman CLI executed two requests with six passing assertions. Browser/HTTP evidence is recorded in test-matrix.md. No human review is claimed. |

Author headers were applied to created/modified source/config files that support comments. JSON and untouched generated Maven wrappers were not modified merely to insert a comment. Local credentials were generated into ignored application-local.properties, never placed in repository documentation/evidence. Security/JWT and later workflows are planned; no AI-generated mock data replaces backend responses.

| 2026-11-04 / A3-02 | OpenAI Codex | JWT authentication, BCrypt registration, configured staff login, role authorization and own profile APIs. | Agent executed the stage-specific tests and inspected actual output/evidence. No human review is claimed. See test-matrix.md and evidence/a3-02-*. |

| 2026-11-04 / A3-03 | OpenAI Codex | Staff customer/room CRUD and search, room-type lookup, referenced-room deactivation and customer-history protection. | Agent executed the stage-specific tests and inspected actual output/evidence. No human review is claimed. See test-matrix.md and evidence/a3-03-*. |
