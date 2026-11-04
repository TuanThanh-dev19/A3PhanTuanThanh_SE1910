<!-- Author: Phan Tuan Thanh - CE190595 -->

# Debug log

| Date/stage | Observed symptom | Confirmed cause | Fix and retest | Related commit |
| --- | --- | --- | --- | --- |
| 2026-11-04 / A3-01 | First `mvn clean verify` failed to delete the packaged JAR | The A3 bootstrap Java process still held the JAR open on Windows | Stopped the verified A3 process; clean verify then passed all seven tests | A3-01 SHA recorded in development-plan.md after commit |
| 2026-11-04 / A3-01 | Default-profile startup failed on port 8080; proxy received an unrelated 401 response | Startup log explicitly reported 8080 already in use by another application | Changed the A3 default/proxy to 8081; preserve the other application; final startup/browser result recorded in evidence | A3-01 SHA recorded in development-plan.md after commit |
| 2026-11-04 / A3-01 | HTTP verification script reported too few seeded rows while the API returned three rooms | Wrapping Invoke-RestMethod in @() produced one nested Object[]; reproduced wrapped count 1 versus direct count 3 | Assigned the JSON response directly; verification script passed room/type counts and proxy comparisons | A3-01 SHA recorded in development-plan.md after commit |

No known application defect remains from the executed A3-01 tests. Later-stage features remain unimplemented and untested. Java 21 prints a Mockito dynamic-agent warning during tests; it does not cause a test failure.

A3-02: Maven repackage encountered the existing A3-01 JAR lock. Stopped that verified process, reran clean verify successfully and added scripts/start-backend.ps1 to run an ignored JAR copy. The seven authentication tests and seven bootstrap regressions passed after the fix.
