# Guardian SDK integration audit

日期：2026-10-23

## Scope and baseline

- Initial audit snapshot: parent HEAD `b73ac4bcc1f1811c705095ec5694a05f31942757`, SDK HEAD `fe45110863b2a51999bb767a5610f0f8e69ac4bf`.
- 409 implementation task baseline: parent HEAD `3dfe5c4676a3d9cac50f79f008eb74a75b11c5bc`, SDK HEAD `f38d234af0cc6d8eb2e34e80aa8d4a97477a83d4`, both on `main...origin/main`.
- Preserved pre-existing worktree state: parent `D frontend_design.md`, untracked `backend/.tmp/` and `backend/tools/docs/`.
- NextPlugins was inspected and built read-only at HEAD `c34ff7d59ce6c4e156cc60dd89f65255188fe82e`; no NextPlugins source or shared documentation was modified.

## Authority and consumption chain

1. `../contracts/newnanmanager.openapi.json` is the HTTP contract authority.
2. `codegen/manifest.json` pins OpenAPI Generator CLI `2.41.0` and engine `7.25.0`; Java uses the `java` plus `okhttp-gson` generator in `codegen/manifest.json`.
3. The generated transport exposes `createServerSession`, `validate`, `heartbeat`, and `setPlayersOffline` with the required session headers.
4. The Java facade creates `SessionContext` and forwards its ID/epoch through the generated transport. Guardian consumes the facade coordinate `com.newnancity:newnanmanager-java:2.0.0`, which transitively needs the parent POM and `com.newnancity:newnanmanager-generated-java:2.0.0`.

## Verification evidence

- `python -B codegen/generate.py --validate-only --all`: OpenAPI contract valid.
- `python -B codegen/generate.py --all --check` after regeneration: all six generated outputs up to date; the contract change affected only the three API files in each of Go, Python, C#, and Java.
- `python -B -m unittest discover -s codegen -p "test*.py" -v`: 12 tests passed, including the session-conflict contract regression.
- Maven reactor `test`: 9 facade tests, 0 failures; Python passed 16 tests, Go test/vet passed, and C# passed 20 tests.
- Generated Go compiled with `go test ./...`, generated Python compiled with `compileall`, and generated C# built with 0 warnings/errors.
- The complete SDK reactor deployed to a temporary file Maven repository, then that repository was served only on loopback HTTP. Guardian resolved the facade, generated Java client, parent POM, and transitive dependencies without mavenLocal.
- Guardian `:plugins:guardian:paper:dependencies --configuration runtimeClasspath`: BUILD SUCCESSFUL, resolving `com.newnancity:newnanmanager-java:2.0.0` and its generated transport.
- Guardian `:plugins:guardian:paper:check`: BUILD SUCCESSFUL, including package audit and `verifyManagerSdkBundle` packaged-runtime probe.

### Original evidence export

The four missing command outputs were recovered from the original Codex session transcript rather than rerun. The transcript is `C:\Users\nmg_w\.codex\sessions\2026\10\23\rollout-2026-10-23T08-24-49-01a16625-6528-7bb3-ae94-f3c743edeb1a.jsonl`; the exported logs and source line/command/cwd/exit-code manifest are under `C:\Users\nmg_w\AppData\Local\Codex\private-reports\newnan-409-20261023-1510`:

- `original-generator-all-check.log`: exit 0; all six outputs up to date.
- `original-python-tests.log`: exit 0; 16 tests ran with the unittest runner's plain `OK` result, so the original output reports no failures, errors, or skips.
- `original-go-test-vet.log`: exit 0; the three reported Go packages passed and `go vet` produced no diagnostics.
- `original-csharp-tests.log`: exit 0; 20 passed, 0 failed, 0 skipped.
- `original-generator-precheck-drift.log`: exit 1 before regeneration, recording the expected pre-fix drift.
- `original-csharp-no-restore.log`: exit 1 because `Tests/obj/project.assets.json` was absent; the subsequent existing-project restore/test command produced the passing C# log above.
- `original-evidence-manifest.json`: exact source line, timestamp, command, cwd, exit code, duration, log path and SHA-256 for each export; manifest SHA-256 is `50102321A7E0EDFB89F7C97CFE25B1660AEF06258DEAA3356F380C3C9CE9A134`.

Codex's original `CommandExecution.aggregated_output` combines stdout and stderr; the exported `.log` files preserve that combined stream verbatim rather than claiming separate streams were retained by the original runner.

## Findings

### No NewNanManager SDK build or session-forwarding blocker

The typed Java entry points forward `X-NNM-Session-ID` and `X-NNM-Session-Epoch`; existing loopback tests cover both string and `SessionContext` overloads. The generated output is current, the reactor builds, and the packaged Guardian runtime can load and call the SDK.

### OpenAPI response-coverage defect resolved after approval

The backend runtime test rotates a server session and proves the old session receives HTTP 409 (`backend/internal/platform/httpserver/mysql_runtime_test.go:227-237` in the parent checkout). The original audit found that canonical OpenAPI omitted 409 for:

- `validate`: `../contracts/newnanmanager.openapi.json:878-997`
- `heartbeat`: `../contracts/newnanmanager.openapi.json:2175-2316`
- `setPlayersOffline`: `../contracts/newnanmanager.openapi.json:4287-4408`

The same stale-session guard is used by all three paths: validation calls `verifyServerSession` at `backend/internal/adapters/db/mysql/validation.go:38`, heartbeat and offline use the same helper at `backend/internal/adapters/db/mysql/monitor.go:81` and `:205`, and the helper returns `domain.ErrStaleSession` at `backend/internal/adapters/db/mysql/sessions.go:67-80`.

This was an additive public contract gap rather than a current runtime blocker: the generated exception already carried the HTTP code, and `ApiExceptionMapper` maps a structured error to facade `ApiException`, which extends `HttpException` and retains the status. After owner approval, canonical OpenAPI now declares 409 `ErrorResponse` for all three operations, the fixed generation chain updates the Go/Python/C#/Java API files, and TypeScript/Kotlin remain drift-free.

### Guardian lifecycle risk

Guardian intentionally does not renew a fenced session: `plugins/guardian/paper/src/main/kotlin/city/newnan/plugins/guardian/paper/GuardianBackend.kt:187-230` keeps the original session and marks 401/403/409 failures unhealthy. Its tests explicitly require a stale epoch not to create a replacement session (`plugins/guardian/paper/src/test/kotlin/city/newnan/plugins/guardian/paper/GuardianManagerTest.kt:458-473`). This is a consumer lifecycle policy, not an SDK defect, but it makes automated recovery after another server takes the epoch a maintenance/restart requirement.

## Remaining unverified boundary

This audit did not connect to a real Manager API or MySQL, did not run a two-server takeover rehearsal, did not publish to a remote Maven repository, and did not deploy a production artifact. The local package check used a loopback-only repository and synthetic HTTP fixtures. Those remain release acceptance items rather than SDK build blockers.
