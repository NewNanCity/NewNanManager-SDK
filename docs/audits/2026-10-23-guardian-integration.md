# Guardian SDK integration audit

日期：2026-10-23

## Scope and baseline

- Parent checkout: `D:\Developer\Minecraft\NewNanManager`, HEAD `b73ac4bcc1f1811c705095ec5694a05f31942757`, branch `main...origin/main`.
- SDK checkout: `D:\Developer\Minecraft\NewNanManager\sdk`, independent Git root, HEAD `fe45110863b2a51999bb767a5610f0f8e69ac4bf`, branch `main...origin/main`.
- Preserved pre-existing worktree state: parent `D frontend_design.md`, untracked `backend/.tmp/` and `backend/tools/docs/`; SDK untracked `java/target/`.
- NextPlugins was inspected read-only at HEAD `89694a6e3cce485b003b3339e2697c3d9c1206fe` and was not modified.

## Authority and consumption chain

1. `../contracts/newnanmanager.openapi.json` is the HTTP contract authority.
2. `codegen/manifest.json` pins OpenAPI Generator CLI `2.41.0` and engine `7.25.0`; Java uses the `java` plus `okhttp-gson` generator in `codegen/manifest.json`.
3. The generated transport exposes `createServerSession`, `validate`, `heartbeat`, and `setPlayersOffline` with the required session headers.
4. The Java facade creates `SessionContext` and forwards its ID/epoch through the generated transport. Guardian consumes the facade coordinate `com.newnancity:newnanmanager-java:2.0.0`, which transitively needs the parent POM and `com.newnancity:newnanmanager-generated-java:2.0.0`.

## Verification evidence

- `python -B codegen/generate.py --validate-only --all`: OpenAPI contract valid.
- `python -B codegen/generate.py --language java --check`: generated Java up to date.
- Maven reactor `test`: 9 facade tests, 0 failures; generated and facade modules built successfully.
- The complete SDK reactor deployed to a temporary file Maven repository, then that repository was served only on loopback HTTP. Guardian resolved the facade, generated Java client, parent POM, and transitive dependencies without mavenLocal.
- Guardian `:plugins:guardian:paper:dependencies --configuration runtimeClasspath`: BUILD SUCCESSFUL, resolving `com.newnancity:newnanmanager-java:2.0.0` and its generated transport.
- Guardian `:plugins:guardian:paper:check`: BUILD SUCCESSFUL, including package audit and `verifyManagerSdkBundle` packaged-runtime probe.

## Findings

### No NewNanManager SDK build or session-forwarding blocker

The typed Java entry points forward `X-NNM-Session-ID` and `X-NNM-Session-Epoch`; existing loopback tests cover both string and `SessionContext` overloads. The generated output is current, the reactor builds, and the packaged Guardian runtime can load and call the SDK.

### Confirmed OpenAPI response-coverage defect, not a current runtime blocker

The backend runtime test rotates a server session and proves the old session receives HTTP 409 (`backend/internal/platform/httpserver/mysql_runtime_test.go:227-237` in the parent checkout). The canonical OpenAPI currently omits 409 for:

- `validate`: `../contracts/newnanmanager.openapi.json:878-997`
- `heartbeat`: `../contracts/newnanmanager.openapi.json:2175-2316`
- `setPlayersOffline`: `../contracts/newnanmanager.openapi.json:4287-4408`

The same stale-session guard is used by all three paths: validation calls `verifyServerSession` at `backend/internal/adapters/db/mysql/validation.go:38`, heartbeat and offline use the same helper at `backend/internal/adapters/db/mysql/monitor.go:81` and `:205`, and the helper returns `domain.ErrStaleSession` at `backend/internal/adapters/db/mysql/sessions.go:67-80`.

Generated documentation therefore omits that documented server behavior. The generated transport and facade still preserve HTTP 409: the generated exception carries the HTTP code, and `ApiExceptionMapper` maps a structured error to facade `ApiException`, which extends `HttpException` and retains the status. Fixing the contract is an additive public-schema change and remains pending parent-contract-owner approval.

### Guardian lifecycle risk

Guardian intentionally does not renew a fenced session: `plugins/guardian/paper/src/main/kotlin/city/newnan/plugins/guardian/paper/GuardianBackend.kt:187-230` keeps the original session and marks 401/403/409 failures unhealthy. Its tests explicitly require a stale epoch not to create a replacement session (`plugins/guardian/paper/src/test/kotlin/city/newnan/plugins/guardian/paper/GuardianManagerTest.kt:458-473`). This is a consumer lifecycle policy, not an SDK defect, but it makes automated recovery after another server takes the epoch a maintenance/restart requirement.

## Remaining unverified boundary

This audit did not connect to a real Manager API or MySQL, did not run a two-server takeover rehearsal, did not publish to a remote Maven repository, and did not deploy a production artifact. The local package check used a loopback-only repository and synthetic HTTP fixtures. Those remain release acceptance items rather than SDK build blockers.
