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
- Production implementation: not started.
- Local Simulation remains the only executable authority mode for Phase 1.
- No Windows bridge or physical-robot control is authorized in Phase 1.

## Task ledger

### Task 1 — RobotProvider and RobotRegistry
**Status:** pending  
**Implementation commits:** none  
**Review:** not started  
**Tests:** not run  
**Next action:** execute Task 1 from the plan using TDD.

### Task 2 — Capability, profile, and connection-mode models
**Status:** pending

### Task 3 — Simulator/language/project-format adapter contracts
**Status:** pending

### Task 4 — RC+ 7.5.3 / SPEL+ baseline adapters
**Status:** pending

### Task 5 — Canonical SharedRuntime state and commands
**Status:** pending

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
