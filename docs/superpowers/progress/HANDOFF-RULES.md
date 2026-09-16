# Cross-Session Handoff Rules

These rules apply while executing Phase 1 on `feature/shared-runtime-foundation` and should be followed by both Codex and ChatGPT inline sessions.

## GitHub is the durable source of execution state

Do not rely on conversation history as the only record of progress.

The primary execution ledger is:

`docs/superpowers/progress/2026-09-16-shared-runtime-foundation.md`

The active pull request is Draft PR #6.

After each completed task, material fix/review round, blocker, or execution handoff, persist the state to GitHub.

## Chat-context handoff

If a ChatGPT conversation is approaching its practical context limit, use the remaining context to create a handoff before continuing in a new chat.

The handoff must include:

- repository and active branch;
- active PR;
- plan and binding spec paths;
- latest verified branch HEAD;
- tasks completed and their implementation commit SHAs;
- current task and exact sub-step;
- tests/CI run and their current results;
- open failures, blockers, reviewer findings, and rulings;
- files currently being edited, if any;
- whether the branch/working tree is clean;
- exact next action and command;
- explicit reminder to read this file and the persistent execution ledger first.

Persist the same recovery information in GitHub before the old chat ends whenever possible.

## Codex coexistence rule

Before starting or resuming implementation inline, inspect the feature branch, Draft PR #6 comments, and the execution ledger for evidence that Codex is currently advancing the same task.

If Codex has resumed and is actively advancing the branch:

- do not duplicate or race its implementation;
- let Codex continue;
- inspect/persist only coordination state when necessary;
- include in any chat handoff that Codex is active, the last observed commit/task, and that the next chat must re-check GitHub before editing.

If Codex is blocked/paused and has published a handoff, inline execution may resume from the first incomplete task.

## Returning work to Codex

When Codex/Work quota becomes available again, Codex should first read:

1. this file;
2. `docs/superpowers/progress/2026-09-16-shared-runtime-foundation.md`;
3. Draft PR #6 and its newest comments;
4. the feature branch history/diff;
5. the implementation plan and binding spec.

It must resume from the first incomplete task rather than replaying completed tasks.

## No merge during handoffs

Keep PR #6 Draft until all Phase 1 acceptance gates are satisfied. A handoff is not a completion signal and must never trigger a merge.
