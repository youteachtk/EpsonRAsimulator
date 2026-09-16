# Shared Runtime Foundation — Execution Ledger

**Plan:** `docs/superpowers/plans/2026-09-16-shared-runtime-foundation.md`  
**Spec:** `docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`  
**Branch:** `feature/shared-runtime-foundation`  
**Execution mode:** Codex + Superpowers Subagent-Driven Development  
**Purpose:** Persist enough execution state in GitHub that ChatGPT can resume inline if Codex/Work usage is exhausted.

## Mandatory progress-recording rule

Codex must update this file and commit it to the feature branch after every completed task or materially important interruption.

Each task entry must record:
- task number and title;
- status: pending / in-progress / complete / blocked;
- implementation commit SHA(s);
- reviewer verdict: spec compliance + code quality;
- focused test command(s) and result;
- any fix-round commit SHA(s);
- any Superpowers ruling made;
- unresolved concerns or deferred minors;
- exact next task / next action;
- last verified branch HEAD.

Do not rely only on Codex conversation history or a local `.superpowers` workspace. The repository ledger is the cross-session recovery record.

## Current state

- Formal architecture spec: approved.
- Phased implementation sequence: approved.
- Phase 1 executable plan: ready.
- Feature branch: created.
- Production implementation: Task 1 complete and independently approved; Tasks 2–8 pending. See latest handoff entry below.
- Local Simulation remains the only executable authority mode for Phase 1.
- No Windows bridge or physical-robot control is authorized in Phase 1.

## Task ledger

### Task 1 — RobotProvider and RobotRegistry
**Status:** complete  
**Implementation commits:** `2f914f1875979424d282abeb2ce2a2644a1d66f0`  
**Review:** independent Spec compliant / Task quality Approved; no blocking code findings  
**Tests:** focused RED/GREEN and full unit-test command passed; APK signing blocked (see handoff)  
**Next action:** Task 2, fresh implementer and focused CapabilityModelsTest RED first.

### Task 2 — Capability, profile, and connection-mode models
**Status:** complete
**RED commit:** `b54fb6f6de02df72135e356cde89be5feb0ccc27`
**Implementation commits:** `a6c611019f1923a6e7e50b9d03b8ed7591c308dc`, `c382dc24f2c056d8fd171a17e8d4f128b490bc4c`
**Review:** inline plan/spec review passed; all planned model types and enum values are present; non-local modes remain non-executable in Phase 1.
**Tests/CI:** run 97 was the intended RED; run 98 passed after capability models; run 107 passed unit tests, debug APK build and artifact upload after final Task 2 code.
**Next action:** Task 4 after Codex-activity check.

### Task 3 — Simulator/language/project-format adapter contracts
**Status:** complete
**RED commit:** `06dd342fafb97d299aa0a47f0866ca20e63357b3`
**Implementation head:** `d03ce87c42daa0e9ddce0a4800392e3bc24f22d7`
**Review:** inline plan/spec review passed; no Critical/Important findings.
**Tests/CI:** GitHub Actions run 100 failed as expected on unresolved adapter types; run 105 succeeded with unit tests, debug APK build, and APK upload.
**Next action:** Task 4 may proceed after checking Codex activity; Task 2 remains separately incomplete until `ConnectionMode.kt` is published.

### Task 4 — RC+ 7.5.3 / SPEL+ baseline adapters
**Status:** complete
**RED commit:** `be3950885d2582ca74f783e266886caa505eba56`
**Implementation commit:** `3e8803623edd6eada2b149968322c5f787076159`
**Review:** inline plan/spec review passed; exact simulator/language/project-format IDs, School Setup profile, verified extensions, and six baseline capability IDs match the plan.
**Tests/CI:** run 109 failed as intended because the RC+ adapter package did not exist; run 110 succeeded.
**Next action:** Task 5 TDD.

### Task 5 — Canonical SharedRuntime state and commands
**Status:** complete
**RED commit:** `23f588a09b83f476c9148ff0789133a4995c0711`
**Implementation commit:** `1a2715b06e8e85903c33a48ed511785a2059eec5`
**Review:** inline plan/spec review passed; no Critical/Important findings. Independent subagent review unavailable in this session.
**Tests/CI:** run 112 failed as intended because runtime classes did not exist; run 113 passed unit tests, debug APK build, and artifact upload.
**Extra ruling coverage:** initial joint states must match robot count, be finite, and remain within configured limits; command joint values/states reject non-finite values; finite out-of-range command values still clamp.
**Next action:** Task 6 TDD.

### Task 6 — AppRuntimeFactory composition root
**Status:** pending

### Task 7 — Compose binding to SharedRuntime
**Status:** pending

### Task 8 — Documentation + final verification
**Status:** pending

## Verification required before Phase 1 completion

- focused new unit tests pass;
- full `gradle testDebugUnitTest --stacktrace` passes;
- `gradle assembleDebug --stacktrace` passes;
- manual C4 smoke test passes;
- GitHub Actions green on the feature branch/PR;
- final whole-branch review completed;
- no claim of SPEL+ execution, RC+ Digital Twin bridge, or physical-robot control.

## Resume protocol

If Codex stops because of quota, context loss, or another interruption:

1. Read this ledger.
2. Read the plan and spec paths above.
3. Inspect the branch and latest commit.
4. Resume from the first task not marked complete.
5. Preserve TDD: do not add production behavior without a failing test first.
6. Continue updating this ledger in GitHub after each task/review/fix round.

## Codex start instruction

Execute `docs/superpowers/plans/2026-09-16-shared-runtime-foundation.md` with Superpowers Subagent-Driven Development on `feature/shared-runtime-foundation`.

Use the plan as the implementation procedure and the formal spec as the binding authority. Use a fresh implementer per task, independent task review, TDD, final whole-branch review, and verification-before-completion.

After every task or interruption, update and commit this GitHub ledger before continuing.

## Quota-stop handoff protocol

If Codex is about to stop because Work/Codex quota is exhausted, it must use its final available turn to persist an exact handoff to GitHub before stopping.

Required actions:
1. Finish or safely stop the current atomic step; do not leave an uncommitted half-edit if avoidable.
2. Run the narrowest relevant verification that still fits the remaining budget.
3. Commit all valid code already completed on `feature/shared-runtime-foundation`.
4. Update this ledger with:
   - current task and exact sub-step;
   - what is complete;
   - what remains;
   - latest commit SHA;
   - test commands run and results;
   - any failing test/error/blocker;
   - reviewer findings still open;
   - any Superpowers ruling made;
   - exact files currently being worked on;
   - exact next command/action to resume;
   - whether the working tree is clean or has uncommitted changes.
5. Commit the ledger update.
6. Push the branch.
7. Add a short comment to Draft PR #6 containing:
   - `HANDOFF READY FOR INLINE RESUME`;
   - latest commit SHA;
   - current task number;
   - exact next action;
   - pointer to this ledger.
8. Do not merge the PR.

After that, ChatGPT inline can resume by reading the branch, Draft PR #6, and this ledger.

## Execution events — 2026-09-16

### Setup / resolved access interruption
- Current task: preflight before Task 1; production implementation remains not started.
- Status: in-progress. Last verified branch HEAD: c58387a296be7d2d548568e1b7e5769ccafb7aa8.
- Access: initial Git clone failed with Windows Schannel SEC_E_NO_CREDENTIALS; retry using `git -c http.sslBackend=openssl clone --branch feature/shared-runtime-foundation --single-branch` succeeded after session network permission was granted.
- Verification: exact feature branch and clean tracked checkout verified; PR #6 is open, draft, unmerged. Gradle 9.6.0 downloaded with official SHA256 verified. Baseline `gradle testDebugUnitTest --stacktrace` running; no test result claimed yet.
- Reviewer verdict: preflight independent review in progress; no implementation review yet.
- Open findings: none yet; baseline/build availability being checked.
- Ruling: use this newly cloned, dedicated feature-branch checkout as the isolated workspace — no pre-existing user checkout is touched — cost if wrong: relocate checkout, no code impact.
- Ruling: use equivalent PowerShell artifact generation when bundled Bash scripts fail Windows path resolution — preserves brief/report/diff contracts — cost if wrong: regenerate scratch review packages.
- Files touched: this ledger only (tracked); ignored SDD workspace and external tooling/logs (scratch).
- Next exact action: inspect baseline test result and preflight table, record rulings, then dispatch fresh Task 1 implementer with TDD brief.


### Preflight review and controller rulings
- Current task: preflight before Task 1; status: in-progress.
- Last verified GitHub HEAD: 892db0fe28ac061b8f2f00fa70a4a2631adeb371.
- Reviewer: independent preflight agent; verdict: conditional go with proposed R1/R2/R3 below.
- Tests: initial baseline failed before tests with AccessDeniedException C:\\.android; retry with ANDROID_USER_HOME under workspace now reaches Android resource tasks. No passing baseline claimed yet.
- Publication: local Git push did not update remote; authenticated GitHub connector update_file committed ledger as 892db0f. Local duplicate bookkeeping commit 77e7661 replaced by this remote commit; branch synchronized.
- Ruling: defer preflight R1 profile-keyed capabilities and adapter/profile validation in R2 — Phase 1 plan explicitly supplies identity-only contracts and only one composed School Setup; spec's multi-profile behavior belongs to later capability work — cost if wrong: evolve these contracts later, no currently selectable profiles are lost.
- Ruling: accept R2 finite and valid initial joint-state checks for Task 5 with failing regression tests first — canonical state must respect the existing robot limits — cost if wrong: stricter constructor/input rejection than example code.
- Ruling: accept R3 as documentation of the retained C4 presentation boundary; keep current renderer unchanged — generic provider/runtime support is not generic rendering — cost if wrong: future renderer migration remains required.
- Ruling: providerId remains provenance identity only in Task 1; global robot IDs determine lookup — follows plan API without extra uniqueness policy — cost if wrong: add provider validation later.
- Findings open: baseline verification pending; no production implementation or task reviewer verdict yet.
- Files touched: ledger only; briefs/constraints/preflight report in ignored SDD scratch.
- Next exact action: obtain successful baseline, dispatch Task 1 from task-1-brief.md, run RED/GREEN, publish code and ledger, then independent review.

<details>
<summary>Independent preflight tables and recommendations (recommendations adjudicated above)</summary>

# Shared Runtime Foundation Preflight

Date: 2026-09-16  
Branch: `feature/shared-runtime-foundation`  
Scope: read-only review of the implementation plan, approved design spec, and current repository code. This report is scratch input for execution and is not part of the implementation deliverables.

## Decision

**Conditional go.** The phase boundary is appropriately small, and the task order follows the approved dependency direction. Before implementation, settle three contract issues so the foundation does not encode contradictions that immediately require replacement:

1. Model capabilities per training profile rather than as one simulator-wide set.
2. Make `SharedRuntime` validate its simulator/profile identity and every canonical joint state, including the initial state.
3. Keep the current Compose screen explicitly C4-specific for this phase and narrow the extensibility claim accordingly; do not imply that registering a second robot already makes this screen render it.

These rulings stay inside the foundation phase. They do not add parsing, persistence, tasks, I/O, workcell behavior, RC+ windows, a bridge, or hardware control.

## Authority used

- The approved spec requires a neutral shared runtime, one canonical robot/point state, profile-dependent capability availability, and separate Local Simulation / Digital Twin / Real Hardware authority modes.
- The plan intentionally limits implementation to identities, registries, the current C4 robot state, and Local Simulation execution.
- Existing code confirms that `RobotDefinition.validatedState` clamps joint values, while `C4RobotScene` and `C4Kinematics` remain C4-only. The current UI therefore cannot safely render an arbitrary robot merely because it exists in `RobotRegistry`.

## Required rulings

### R1 — Profile capabilities

The planned `SimulatorAdapter.defaultProfileId` plus one `SimulatorAdapter.capabilities` value cannot represent the spec's central distinction between School Setup and Full Learning. It also gives `AdapterRegistry` nothing to validate for the default profile.

Use the smallest future-safe shape in this phase: expose profiles as a map (or descriptors) owned by the simulator adapter, with one `school-setup` entry now, and require the default profile to be present. Rich capability provenance/fidelity metadata remains deferred.

Example contract shape:

```kotlin
interface SimulatorAdapter {
    val defaultProfileId: TrainingProfileId
    val capabilitiesByProfile: Map<TrainingProfileId, CapabilitySet>
}
```

`AdapterRegistry` should reject an adapter whose default profile is absent. No Full Learning features need to be implemented now.

### R2 — Canonical runtime invariants

The planned runtime checks only the initial joint count. It can therefore publish an out-of-range or non-finite initial state. It also accepts simulator/profile IDs without access to `AdapterRegistry`, so a public `SharedRuntime` can begin with unknown or mismatched IDs.

Pass `AdapterRegistry` (or a resolved/validated simulator profile) into `SharedRuntime`. At construction, require the simulator and profile to exist and require the initial joint values to be finite and within the active robot's limits. Apply the same finite-value rule to `SetJointValue` and `SetJointState`; retain the existing clamp behavior for finite out-of-range commands if that is the intended jog behavior.

The selected simulator/profile may remain fixed session configuration in this phase. Adding simulator/profile switching commands is unnecessary unless the plan continues to claim they are user-selectable now.

### R3 — C4 UI boundary

Task 7 reads a generic `activeRobot()` but immediately passes its state into `C4Kinematics` and `C4RobotScene`, whose current contract requires exactly six values and uses fixed C4 assets/axes. A second registered robot can therefore crash or be rendered incorrectly.

Keep the screen C4-only and make that precondition explicit for this phase. The documentation should say that provider/runtime selection is neutral while the existing trainer presentation remains the C4 presentation adapter. A generic renderer/kinematics provider belongs to a later plan; it should not be pulled into this phase.

## Per-task self-consistency

| Task | Status | Finding | Ruling before execution |
|---|---|---|---|
| 1 — Robot provider/registry | Pass with minor cleanup | Global robot-ID uniqueness is enforced and matches runtime lookup. `providerId` is otherwise unused and duplicate provider IDs are not checked. | Either reject duplicate `providerId`s now or document it as display/provenance-only. Do not add more provider metadata in this phase. |
| 2 — Capability/profile/mode models | Pass as primitives | The value types and reserved connection modes are coherent. On their own they do not establish profile-to-capability association. | Keep these primitives; apply R1 in Task 3. Reserved non-local modes are state vocabulary only. |
| 3 — Adapter contracts/registry | **Change required** | Language and project-format references are validated, but the simulator-wide capability set contradicts profile-specific availability. Tests cover only an unknown language, not duplicate IDs, unknown project format, or the default profile. | Apply R1 and add focused registry tests for each validation branch. Keep parser/project-resource behavior deferred. |
| 4 — RC+ baseline adapters | **Change required through R1** | The IDs, extensions, and six listed capability families are supported by the research inventory. The single capability set has no explicit `school-setup` association. | Register exactly one School Setup profile entry for now. Do not infer installed optional modules or Full Learning availability. |
| 5 — Shared runtime/reducer | **Change required** | Commands preserve a single state and notify observers correctly. Initial values are size-checked only; simulator/profile IDs are unvalidated; NaN can enter via joint commands. | Apply R2. Add tests for unknown simulator/profile, out-of-limit initial state, non-finite values, and unchanged state after a rejected connection-mode command. |
| 6 — Composition root | **Change required through R1/R2** | It creates all three registries/baselines coherently, but `AdapterRegistry` is stored beside the runtime rather than participating in runtime validation. | Resolve and validate the School Setup profile before constructing the runtime, and pass the registry/resolved configuration required by R2. Keep the one C4 + one RC+ baseline. |
| 7 — Compose binding | Pass for C4 after R3 | Subscription lifecycle and command routing remove the screen-owned joint state. The screen remains concretely C4-specific and has no automated binding test. | Apply R3, preserve the manual C4 smoke test, and avoid claiming the screen is multi-robot. No new generic renderer is needed. |
| 8 — Docs/final verification | Pass with execution caveats | The proposed implementation claims are otherwise bounded. Final `git status` will also show this untracked scratch report, and the issue update must follow green CI. | Treat `work/preflight.md` as an acknowledged scratch exception (or remove it after consuming it). Do not stage it. Update Issue #1 only after push/green CI as written. |

## Cross-task interfaces and shared files

Every direct task pair that shares a produced/consumed contract or file is listed below.

| Pair | Shared boundary/file | Conflict risk | Ruling |
|---|---|---|---|
| 1 → 5 | `RobotRegistry`, `RobotDefinition`, joint limits | Runtime validates only joint count initially. | R2: validate the whole initial state against the resolved robot. |
| 1 → 6 | `EpsonRobotProvider`, `RobotRegistry` | None beyond hard-coded baseline selection. | Keep the explicit C4 baseline; factory test proves it. |
| 2 → 3 | `TrainingProfileId`, `CapabilitySet` | A single simulator-wide capability set cannot distinguish profiles. | R1: profile-keyed capabilities and registry validation. |
| 2 → 4 | Capability/profile primitives | RC+ capabilities are not attached to School Setup explicitly. | Put the verified six capability IDs in the `school-setup` profile entry only. |
| 2 → 5 | `TrainingProfileId`, `ConnectionMode` | Runtime accepts an arbitrary profile ID; reserved modes are intentionally non-executable. | Validate the profile; keep non-local transitions rejected without state mutation. |
| 2 → 6 | Profile/mode construction | Factory chooses a default profile without validating that the simulator owns it. | Resolve through the adapter registry before runtime creation. |
| 3 → 4 | Adapter interfaces; shared `AdapterRegistryTest.kt` | This is the only direct same-file edit conflict. Task 4's appended test depends on Task 3's final test structure. | Execute sequentially. Task 4 edits the existing test after Task 3 lands. |
| 3 → 5 | `SimulatorAdapterId` and adapter registry semantics | State carries only an unvalidated adapter ID. | R2: runtime receives registry/resolved configuration. |
| 3 → 6 | `AdapterRegistry` | Registry is composed but otherwise detached from runtime. | Use it to validate the simulator/profile during construction. |
| 4 → 5 | Baseline simulator/profile identifiers | Task 5 duplicates string literals and can drift from Task 4. | In production composition use adapter object IDs; string literals are acceptable in isolated negative/unit fixtures only. |
| 4 → 6 | RC+ adapter singleton objects | No conflict after R1. | Compose the exact verified baseline objects and School Setup entry. |
| 5 → 6 | `SharedRuntime`, `SharedRuntimeState` | Constructor currently lacks the adapter validation dependency. | Apply R2 and update the factory test to prove the state matches the resolved adapter/profile. |
| 5 → 7 | `SharedRuntime`, `RuntimeCommand`, subscription | State flow is coherent. C4-only rendering is outside the neutral runtime contract. | Keep runtime pure Kotlin; enforce/document the presentation precondition in Task 7. |
| 6 → 7 | `AppRuntimeFactory.createDefault()` | `MainActivity` discards the bundle and retains only runtime; this is harmless today because runtime retains robot registry, but adapter data becomes unavailable to future UI. | For this phase, retain one `AppRuntimeBundle` in `remember` and pass `.runtime` to the C4 screen. This preserves the composition root without expanding UI. |

## Execution ordering and staging

The numbered order is safe. If work is split, only Tasks 1 and 2 are initially independent. Then run Task 3, Task 4, Task 5, Task 6, Task 7, and Task 8 in dependency order.

The plan's broad staging commands are unsafe under parallel edits:

- Task 2 and Task 5 both stage the whole `runtime` directory; Task 5 can accidentally absorb Task 6 files.
- Task 3 stages the whole `adapters` directory and Task 4 extends the same test file.

Stage explicit files for each commit, or complete and commit dependencies sequentially. Do not stage `work/preflight.md`.

## Phase boundary preserved

No preflight finding requires adding source parsing, project persistence, task scheduling, I/O, simulation clock, workcell actors, RC+ workspace/window registries, Digital Twin transport, or real-hardware behavior. Those remain in later plans.

</details>

### Baseline verified / Task 1 dispatched
- Current task: Task 1 RobotProvider/RobotRegistry, TDD RED step; status: in-progress.
- Last verified GitHub HEAD: e1f22ab29de62b5cc0fbe04add58b839c09a0d81.
- Tests: `gradle testDebugUnitTest --stacktrace` with workspace GRADLE_USER_HOME and ANDROID_USER_HOME: BUILD SUCCESSFUL, 24 tasks executed, exit 0. Kotlin daemon emitted AccessDeniedException for its user-profile marker directory and compiler fallback completed successfully. Subsequent runs use `-Pkotlin.compiler.execution.strategy=in-process` to avoid that environment warning.
- Reviewer verdict: preflight recorded above; Task 1 independent review pending implementation.
- Findings open: no code findings; local Git push unavailable, use authenticated GitHub connector commits and synchronize checkout.
- Ruling: run downloaded Gradle 9.6.0 with installed JDK 23 while retaining repository Java source/target 17 — baseline verifies compatibility, repository CI still uses JDK 17 — cost if wrong: CI catches JDK-specific discrepancy before completion.
- Files touched: ledger; external untracked build helper/caches/logs only. No production code completed yet.
- Next exact action: await fresh Task 1 implementer RED/GREEN report, publish its valid commit and ledger, then dispatch independent task reviewer.

### Verification blocker / quota checkpoint
- Current task: Task 1, implementation GREEN reached, self-review/commit/report pending; status: in-progress.
- Last verified remote HEAD: f51e4f0e892aa10f7bc8eb926c092516010a33f3; no production commit yet.
- Tests: focused RobotRegistryTest GREEN (`BUILD SUCCESSFUL in 22s`); full testDebugUnitTest GREEN (`BUILD SUCCESSFUL in 19s`). Detailed RED/GREEN report being finalized.
- Additional assembleDebug attempt FAILED at validateSigningDebug: `java.nio.file.AccessDeniedException` for workspace `work/android-home/debug.keystore.lock`. This is an environment blocker; APK completion not claimed.
- Manual device probe: adb devices failed trying to mkdir `\\.android`; no manual smoke test performed.
- Reviewer verdict: Task 1 independent review pending; no open code findings yet.
- Quota: 74% of five-hour window consumed; preserve remaining budget for Task 1 review/publication and quota-stop handoff. No new task should start if it threatens handoff completion.
- Files touched: four Task 1 Kotlin files currently uncommitted; ledger updated via GitHub connector; generated .kotlin cache remains untracked and must not be committed.
- Rulings: no new architecture ruling; signing/ADB failures remain explicit verification gaps.
- Next exact action: obtain Task 1 valid implementation commit/report; independent review; publish reviewed task and clean handoff if quota threshold reached.

### Task 1 implementation completed / independent review running
- Status: implementation complete; task acceptance pending independent review.
- GitHub implementation commit: 2f914f1875979424d282abeb2ce2a2644a1d66f0. Local original commit: 3747723; authenticated connector published identical task content above latest ledger via non-force ref update.
- Tests: `gradle -Pkotlin.compiler.execution.strategy=in-process :app:testDebugUnitTest --tests mx.youteachtk.epsonrasimulator.robot.RobotRegistryTest --stacktrace`: RED exit 1 unresolved RobotProvider before production; GREEN BUILD SUCCESSFUL (22s). Full `:app:testDebugUnitTest --stacktrace`: BUILD SUCCESSFUL (19s). `git diff --check e1f22ab HEAD`: exit 0.
- Behavior covered: cross-provider lookup, duplicate global robot-ID rejection, missing require rejection, Epson provider wraps unchanged catalog.
- Reviewer verdict: independent task reviewer dispatched; pending.
- Findings open: APK validateSigningDebug AccessDeniedException and ADB environment restriction; overlap was implementer hypothesis, not proven root cause. No production correctness findings yet.
- Rulings: no new ones; providerId remains provenance-only as recorded.
- Files touched: robot/RobotProvider.kt, robot/RobotRegistry.kt, robot/EpsonRobotProvider.kt, test robot/RobotRegistryTest.kt (all under existing app Kotlin package roots); ledger.
- Next exact action: record independent Task 1 review verdict, fix/re-review if necessary, then execute quota-stop handoff before starting Task 2 if remaining budget is insufficient.

### Task 1 independent review — accepted
- Current task: Task 1 complete (implementation 2f914f1875979424d282abeb2ce2a2644a1d66f0); next task: Task 2.
- Last verified branch HEAD before this review ledger commit: fbb2d2091ba8ec4fe9c7b3251fe8f7059ae00940.
- Reviewer verdict: Spec compliant; Task quality Approved. Critical: none. Important: none.
- Evidence: registry implements planned lookup and deterministic global duplicate rejection; provider wraps unchanged C4 catalog; four meaningful RobotRegistryTest cases pass. Focused GREEN and full unit-test command logs report BUILD SUCCESSFUL. Controller git diff --check passed.
- Minor finding: implementer report attributed APK signing denial to overlapping invocations without supporting evidence. Corrected report to root cause unknown; durable ledger likewise records only AccessDeniedException. No production fix round required.
- Open findings: APK build signing lock access denial, ADB home-directory permission failure, manual C4 smoke test not performed; GitHub CI/final whole-branch review pending. These remain final acceptance gates, not Task 1 code defects.
- Rulings: none new.
- Files touched by review: ledger; ignored report wording corrected; generated .kotlin cache moved inside ignored plan scratch after path-boundary verification.
- Working tree: clean, no tracked or untracked changes; branch synchronized with GitHub before this ledger update.
- Next exact action: quota-stop handoff, then fresh Task 2 implementer writes CapabilityModelsTest first and runs focused RED before adding CapabilityModels.kt / ConnectionMode.kt.

## HANDOFF — quota-stop, 2026-09-16

- **Status:** safely paused before Task 2; quota last checked at 88% consumed in five-hour window. Phase 1 is NOT complete.
- **Current task/sub-step:** Task 1 accepted; Task 2 not started. No implementer or reviewer remains running.
- **Completed:** repository isolated on required feature branch; preflight review/rulings recorded; Task 1 RobotProvider/RobotRegistry/EpsonRobotProvider and four focused tests implemented, independently approved, published.
- **Latest verified GitHub commit before this handoff ledger commit:** `b922f7e180caca0aefc21ff6b3f5c829d03f3eb5`. **Implementation SHA:** `2f914f1875979424d282abeb2ce2a2644a1d66f0`. The PR handoff comment names the resulting final ledger commit SHA (a commit cannot embed its own SHA).
- **Tests:** Task 1 focused RED exit 1 (missing RobotProvider before code); focused GREEN BUILD SUCCESSFUL (four tests); full `:app:testDebugUnitTest --stacktrace` BUILD SUCCESSFUL. Commands use Gradle 9.6.0 plus `-Pkotlin.compiler.execution.strategy=in-process`, installed JDK 23, source/target 17, workspace Gradle/Android homes. `git diff --check` passed. CI result is not claimed green; inspect current head Actions on resume.
- **Errors/pending verification:** `:app:assembleDebug --stacktrace` FAILED at validateSigningDebug, AccessDeniedException on workspace `work/android-home/debug.keystore.lock`; root cause unknown. `adb devices` failed creating `\\.android`. Manual nine-step C4 smoke test not run. Full final build/tests, GitHub Actions, Task 8 docs/Issue #1 update, and global final review remain pending.
- **Reviewer findings:** no Critical/Important Task 1 code findings. Minor unsupported attribution of signing failure was corrected; no open code findings. No fix rounds needed.
- **Rulings to preserve:** dedicated clone is isolated workspace; PowerShell equivalents for Bash artifact scripts; providerId is provenance-only; defer profile-keyed capabilities/adapter-profile validation to later work per Phase 1 identity scope; Task 5 must TDD finite and valid initial joint states; current presentation remains C4-only; local JDK23 permitted with source/target17, CI JDK17 remains required.
- **Files completed:** `app/src/main/java/mx/youteachtk/epsonrasimulator/robot/{RobotProvider,RobotRegistry,EpsonRobotProvider}.kt`; `app/src/test/java/mx/youteachtk/epsonrasimulator/robot/RobotRegistryTest.kt`; this ledger. No half-written source remains.
- **Working tree:** clean before final ledger synchronization. Generated Kotlin cache moved into ignored plan scratch; tooling/logs outside repo in parent `work/`. No user files deleted. Scratch is supplemental only; this ledger contains recovery essentials.
- **Publication:** Git CLI clone/fetch works with `-c http.sslBackend=openssl`; Git CLI push did not update remote. Authenticated GitHub connector created commits and advanced ONLY `feature/shared-runtime-foundation` with `force=false`; local duplicate implementation commit was recognized/skipped during rebase onto identical remote patch. All completed source is remote. Continue connector publication if local push remains unavailable.
- **Exact next action:** fetch/synchronize `feature/shared-runtime-foundation`, read this ledger and authority spec, dispatch a fresh Task 2 implementer for the plan's Task 2. Write `app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/CapabilityModelsTest.kt`, run `gradle :app:testDebugUnitTest --tests mx.youteachtk.epsonrasimulator.runtime.CapabilityModelsTest --stacktrace` and verify expected RED before implementing. On this Windows checkout use `& '..\\run-gradle.ps1'` in place of `gradle` (helper sets workspace homes and in-process compiler). Then GREEN, code+ledger commit/publication, independent review, ledger commit; continue Tasks 3–8 with fresh implementers and fix loops.
- **Final gates:** resolve signing and ADB environment before claiming build/manual smoke success; obtain green GitHub Actions; final independent whole-branch review and verification-before-completion. Keep PR #6 Draft; do not merge. Do not implement Digital Twin or physical robot control.

### Inline resume — Task 2 partial progress
- Execution mode: ChatGPT inline resumed from Codex quota handoff.
- Codex activity check: no Codex commits/comments observed after its handoff; no concurrent Codex worker detected. The branch movement during inline work was caused by the first half of an inline connector call, not Codex.
- Cross-session rules added in `docs/superpowers/progress/HANDOFF-RULES.md` at commit `5ee2416daac9358650821a06c4a8042155487a90`.
- Task 2 TDD RED: `CapabilityModelsTest.kt` committed as `b54fb6f6de02df72135e356cde89be5feb0ccc27`; GitHub Actions run 97 failed at Unit tests with unresolved `CapabilityId` / `CapabilitySet`, confirming the intended RED.
- Task 2 GREEN (partial): `CapabilityModels.kt` committed as `a6c611019f1923a6e7e50b9d03b8ed7591c308dc`; GitHub Actions run 98 completed successfully, including unit tests and debug APK build.
- Remaining Task 2 file: `runtime/ConnectionMode.kt` with exact planned enum values `LOCAL_SIMULATION`, `RCPLUS_DIGITAL_TWIN`, `REAL_HARDWARE`. This is vocabulary only; Phase 1 must not add bridge/hardware execution.
- Publication blocker: the current ChatGPT GitHub mutation safety layer rejected attempts to link/publish the ConnectionMode file. An orphan Git object may exist, but branch HEAD remains `a6c611019f1923a6e7e50b9d03b8ed7591c308dc`; do not rely on orphan objects. No force update was performed.
- Ruling: do not weaken/rename the approved enum just to evade the tooling restriction. Preserve the exact planned contract and hand this one-file publication to Codex if the restriction persists.
- Exact next action on Codex return: re-read ledger + HANDOFF-RULES, confirm branch HEAD, add the exact planned `ConnectionMode.kt`, run focused `CapabilityModelsTest` plus CI, independently review Task 2, update ledger, then continue Task 3.
- If inline continues before Codex returns: it may work only on independent areas that do not require `ConnectionMode`; before every write, re-check PR #6 head for Codex activity and do not race an active Codex worker.

### Inline Task 3 review — accepted
- TDD RED evidence: GitHub Actions run 100 failed in Unit tests because `ProgrammingLanguageAdapter`, `ProjectFormatAdapter`, `SimulatorAdapter`, adapter IDs, and `AdapterRegistry` did not exist.
- GREEN implementation commits culminate at `d03ce87c42daa0e9ddce0a4800392e3bc24f22d7`; changed production files are exactly the five Task 3 adapter-contract/registry files.
- Verification: GitHub Actions run 105 completed successfully: Unit tests success, Build debug APK success, Upload debug APK success.
- Inline review against the Task 3 plan: required IDs, interfaces, duplicate-ID validation, language/project-format reference validation, `requireSimulator`, `languageFor`, and `projectFormatFor` are present. No extra parser/project behavior was added.
- Review limitation: this ChatGPT inline session cannot provide the independent subagent reviewer used by Codex; Codex may re-review Task 3 on return, but should not reimplement it unless it finds a concrete defect.
- Publication note: several mutations were temporarily rejected by the connector; all accepted Task 3 files are now on the branch. No force ref update was used.
- Exact next action: re-check Codex activity. If still paused, try to complete Task 2's exact `ConnectionMode.kt`; otherwise let Codex take over from this ledger. Task 4 is dependency-safe with respect to Task 3 but Task 5 must not start until Task 2 is complete.

### Inline Task 2 completion checkpoint
- Task 2 is complete and reviewed inline.
- Final implementation commits: capability/profile models `a6c611019f1923a6e7e50b9d03b8ed7591c308dc`; connection-mode vocabulary `c382dc24f2c056d8fd171a17e8d4f128b490bc4c`.
- GitHub Actions run 107 succeeded through unit tests, debug APK build, and artifact upload.
- Phase 1 execution authority remains Local Simulation only.

### Inline Task 4 completion checkpoint
- Task 4 is complete and reviewed inline.
- GitHub Actions run 110 completed successfully on `3e8803623edd6eada2b149968322c5f787076159`.
- The implementation adds descriptor metadata only; no parser, bridge, controller, or robot execution behavior was introduced.
- Codex activity check before Task 5: no newer Codex commit/comment detected; inline execution may continue.

### Inline Task 5 completion checkpoint
- Task 5 complete at `1a2715b06e8e85903c33a48ed511785a2059eec5`.
- GitHub Actions run 113 succeeded end-to-end.
- Non-local connection mode dispatch throws before state mutation; Local Simulation remains the only executable authority.
- Review limitation: no subagent reviewer tool is exposed in this chat; Codex may re-review later but should not reimplement without a concrete finding.
