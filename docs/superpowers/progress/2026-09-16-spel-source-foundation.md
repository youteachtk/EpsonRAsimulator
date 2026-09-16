# Phase 2 Execution Ledger — Source-Preserving SPEL+ Foundation

**Date:** 2026-09-16
**Branch:** `feature/source-preserving-spel-foundation`
**Base:** accepted Phase 1 head `8a9a0770f7522bd2ae58b053b796d51403258c1e`
**Draft PR:** #8
**Plan:** `docs/superpowers/plans/2026-09-16-source-preserving-spel-foundation.md`
**Spec:** `docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`

## Durable-state rule

GitHub is the authoritative execution record. After every completed task, review/fix round, blocker, Codex interruption, or chat-context handoff, update this ledger and commit it.

Before any inline/Codex worker edits:
1. read this ledger;
2. read the Phase 2 plan and binding spec;
3. inspect Draft PR #8 head/comments;
4. check for concurrent Codex/inline activity;
5. do not duplicate an active worker.

If Codex becomes active while inline work is running, stop before the next write, let Codex continue, and record the observed head/task.

## Quota/context handoff requirements

A handoff must record:
- current task and exact sub-step;
- completed commit SHAs;
- RED/GREEN test evidence;
- review verdict/findings;
- blockers/rulings;
- current branch HEAD;
- exact next action;
- whether Codex appears active.

Codex quota-stop comment on PR #8 must begin:
`HANDOFF READY FOR INLINE RESUME`

Chat-context handoff must tell the next chat to re-check GitHub/Codex before editing.

## Scope rulings

- Native source is authoritative and must remain character-lossless through tokenization.
- Unknown SPEL+ source becomes Direct Code; unsupported is not equivalent to invalid.
- Locally generated parser diagnostics are not Epson-native controller errors.
- Phase 2 does not execute SPEL+.
- `.sprj` and `.pts` are preserved, not semantically rewritten.
- Phase 2 does not address C4 self-collision; that remains Issue #7.
- No bridge or physical robot control.

## Tasks

### Task 1 — Source ranges and lossless SPEL+ lexer
**Status:** complete

Evidence:
- RED commit: `5bf7c377c3225f64bb5cc27d7726876cc2120660` (`test: add failing source range tests`).
- RED CI: Android CI run #127 failed in Unit tests with `Unresolved reference 'SourceRange'`.
- GREEN implementation commit: `46914c3e09503705299c57d06adae3bf6cab79e3` (`feat: add lossless SPEL source lexer`).
- GREEN CI: Android CI run #128 completed successfully.
- Unit tests: success.
- Debug APK build: success.
- Debug APK upload: success.
- Implemented `SourceRange`, `SourceToken`, `SpelTokenKind`, `SpelLexer`, and `SpelLexerTest`.
- Verified by tests: token concatenation reconstructs source exactly; ranges are contiguous; CR/LF is preserved; apostrophe inside a string is not treated as a comment; comments remain trivia; unknown characters are retained as symbols.

### Task 2 — Conservative SPEL+ semantic model and analyzer
**Status:** complete

Evidence:
- RED commit: `b7f5ffc117c6c932c94b93f10ffcf3317020d642` (`test: add failing SPEL analyzer tests`).
- RED CI: Android CI run #130 failed in Unit tests with unresolved references for `SpelAnalyzer`, `SpelProgramSemanticModel`, `SpelStatement`, and `DiagnosticSeverity`.
- GREEN implementation commit: `9a03a7df39f3b2621e93ac483cee17e6c4b46d48` (`feat: add conservative SPEL semantic analyzer`).
- GREEN CI: Android CI run #131 completed successfully.
- Unit tests: success.
- Debug APK build: success.
- Debug APK upload: success.
- Implemented neutral `ProgramSemanticModel` and `ProgramDiagnostic` contracts.
- Implemented conservative SPEL+ semantics for `Function...Fend`, `Call`, `Go`, `Move`, `Speed`, and `Wait`.
- Unknown nonblank SPEL+ statements remain `DirectCode` instead of becoming parser errors.
- Structural diagnostics cover unclosed functions, stray `Fend`, missing function names, nested functions, and missing operands.
- Recognized operand ranges exclude trailing whitespace/comments and preserve exact source offsets.

### Task 3 — ProgramDocument and last-valid semantic retention
**Status:** complete

Evidence:
- RED commit: `b2c8f2e4bf7e1cfcf585eec0dd829cca398d2872` (`test: add failing program document session tests`).
- RED CI: Android CI run #133 failed in Unit tests with unresolved references for `ProgramDocumentSession` and `ProgramSupportState`.
- GREEN implementation commit: `9ea3edf1127c64b2f2f0b43df6720c847aa9e7f5` (`feat: preserve last valid SPEL semantics`).
- GREEN CI: Android CI run #134 completed successfully.
- Unit tests: success.
- Debug APK build: success.
- Debug APK upload: success.
- Implemented `ProgramDocument`, `ProgramAnalyzer`, `ProgramDocumentSession`, and `ProgramSupportState`.
- Syntax-invalid edits preserve exact source/tokens while setting `semanticModel = null` and retaining the prior valid semantic model in `lastValidSemanticModel`.
- Direct Code documents are `PARTIALLY_SUPPORTED` without losing valid semantics.
- Fully recognized documents are `SUPPORTED`.
- Valid edits replace the remembered last-valid semantic model with the new semantic model.
- `NATIVE_VALID_NOT_LOCALLY_SIMULATABLE` remains deliberately unused without external/native validation evidence.

### Task 4 — Source-preserving semantic operand edits
**Status:** pending

### Task 5 — Source-capable SPEL+ adapter registry wiring
**Status:** pending

### Task 6 — Native RC+ project-resource categories/classifier
**Status:** pending

### Task 7 — Native resource-set round-trip
**Status:** pending

### Task 8 — Documentation and final verification
**Status:** pending

## Verification gates

Before Phase 2 may be accepted:
- focused RED/GREEN evidence for Tasks 1–7;
- `gradle testDebugUnitTest --stacktrace` green;
- `gradle assembleDebug --stacktrace` green;
- PR #8 Actions green on final head;
- final whole-branch review;
- no destructive source rewrite;
- no `.sprj` parser/execution/hardware claims;
- Draft PR remains unmerged unless user explicitly chooses integration.

## Current checkpoint

- Phase 1 accepted and preserved on its own branch.
- Phase 2 branch created from exact accepted Phase 1 head.
- Phase 2 implementation plan committed as `f59fa75781e84c725ad57639942059024801288d`.
- Draft PR #8 uses base `feature/shared-runtime-foundation`.
- Task 1 completed with RED/GREEN evidence and full Android CI green.
- Task 2 completed with RED/GREEN evidence and full Android CI green.
- Task 3 completed with RED/GREEN evidence and full Android CI green.
- Implementation head before this ledger update: `9ea3edf1127c64b2f2f0b43df6720c847aa9e7f5`.
- No concurrent Codex activity was observed before Task 3 edits; inline execution performed the task.
- Exact next action: Task 4 Step 1 — add failing source-preservation tests proving that replacing a recognized statement operand changes only that operand range and leaves whitespace, comments, CRLF, and Direct Code byte-for-byte unchanged.
