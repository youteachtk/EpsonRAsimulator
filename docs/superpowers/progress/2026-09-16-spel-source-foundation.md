# Phase 2 Execution Ledger — Source-Preserving SPEL+ Foundation

**Date:** 2026-09-16
**Branch:** `feature/source-preserving-spel-foundation`
**Base:** accepted Phase 1 head `8a9a0770f7522bd2ae58b053b796d51403258c1e`
**Draft PR:** #8
**Plan:** `docs/superpowers/plans/2026-09-16-source-preserving-spel-foundation.md`
**Spec:** `docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`

## Durable handoff rule

GitHub is the authoritative execution record. Before Codex or an inline chat edits this phase, read this ledger, the Phase 2 plan/spec, and Draft PR #8; then verify whether another worker is active. Do not duplicate an active worker. A Codex quota-stop PR comment must begin `HANDOFF READY FOR INLINE RESUME`.

## Scope rulings

- Native source text is authoritative and must remain character-lossless through tokenization.
- Unknown SPEL+ source is preserved as Direct Code; unsupported is not equivalent to invalid.
- Local parser diagnostics are not Epson-native controller errors.
- Phase 2 does not execute SPEL+, emulate RC+ Build/Run, implement TaskRuntime/I/O, or control hardware.
- `.sprj` and `.pts` contents are preserved, not semantically parsed or rewritten.
- Legacy `domain/ProgramModels.kt` remains untouched.
- C4 self-collision remains Issue #7 and is outside this phase.

## Tasks and evidence

### Task 1 — Source ranges and lossless SPEL+ lexer
**Status:** complete
- RED: `5bf7c377c3225f64bb5cc27d7726876cc2120660`; CI run #127 failed on missing `SourceRange`.
- GREEN: `46914c3e09503705299c57d06adae3bf6cab79e3`; CI run #128 success.
- Verified exact token concatenation, contiguous source ranges, CR/LF preservation, comments, strings containing apostrophes, and unknown-symbol preservation.

### Task 2 — Conservative SPEL+ semantic model and analyzer
**Status:** complete
- RED: `b7f5ffc117c6c932c94b93f10ffcf3317020d642`; CI run #130 failed on missing analyzer/model types.
- GREEN: `9a03a7df39f3b2621e93ac483cee17e6c4b46d48`; CI run #131 success.
- Recognized subset: `Function...Fend`, `Call`, `Go`, `Move`, `Speed`, `Wait`.
- Unknown statements remain Direct Code; structural diagnostics cover unclosed/nested functions, stray `Fend`, missing function names and missing operands.

### Task 3 — ProgramDocument and last-valid semantic retention
**Status:** complete
- RED: `b2c8f2e4bf7e1cfcf585eec0dd829cca398d2872`; CI run #133 failed on missing document/session types.
- GREEN: `9ea3edf1127c64b2f2f0b43df6720c847aa9e7f5`; CI run #134 success.
- Syntax-invalid edits keep exact current source/tokens and retain the prior valid semantic model.
- Direct Code yields `PARTIALLY_SUPPORTED`; recognized-only source yields `SUPPORTED`.
- `NATIVE_VALID_NOT_LOCALLY_SIMULATABLE` is deliberately not assigned without external/native validation evidence.

### Task 4 — Source-preserving semantic operand edits
**Status:** complete
- RED: `5b45cfd6d061effe16c687461515e52c4937ad35`; CI run #136 failed on missing source-edit types.
- GREEN: `633e5e8f8bfb3209bc68df30fd001195a90f0440`; CI run #137 success.
- Editing `Speed 50` to `Speed 75` changes only the argument range while preserving whitespace, CRLF, comments and unknown Direct Code exactly.
- Out-of-bounds edits are rejected.

### Task 5 — Source-capable SPEL+ adapter registry wiring
**Status:** complete
- RED: `e65e9cbd042e233d3a5d1dddf15e2261edf3a339`; CI run #139 failed on missing `sourceLanguageFor`.
- GREEN: `8b09d9ab10e445b708410b64a61be4d4a948c99e`; CI run #140 success.
- Added `SourceProgrammingLanguageAdapter`; RC+ 7.5.3 resolves SPEL+ through the registry and opens a lossless `ProgramDocumentSession`.

### Task 6 — Native RC+ project-resource categories/classifier
**Status:** complete
- RED: `db9939694d1c0d2f8b4f0cdb7c58caa7d244ccea`; CI run #142 failed on missing resource/classifier types.
- GREEN: `4275c41973d3c04e6bf3bb08de95d4573ffa9d0a` + `ad96d02d68ec3e1cbb70a444cc38d59ea7739e08`; CI run #144 success.
- `.prg`/`.inc` are known editable; `.pts`/`.mac`/`.sprj`, `IOLABEL.DAT`, `USERERRORS.DAT` are known preserved; unknown files are opaque.
- Classification is filename/extension-only and resource bytes are defensively copied.

### Task 7 — Native resource-set round-trip
**Status:** complete
- RED: `60fa411531600335d6dd5a95e530a1570362f5e4`; CI run #146 failed because `NativeProjectResourceSet` did not exist.
- GREEN implementation culminates at `e29dad9a084067054df165a7ead939630313fde3`; CI run #149 success.
- Untouched known and opaque files export byte-for-byte identical.
- Supported `.prg` edit changes only program bytes while `.pts` and opaque files remain identical.
- `replaceEditable` rejects preserved/opaque resources and unknown paths.
- Import/export and resource byte access use defensive copies.

### Task 8 — Documentation and final verification
**Status:** implementation/review complete; this ledger commit itself requires final CI confirmation
- Documentation: `bce1bb2ac3c8b4aae02b2926429f0937942986e4` (`ROADMAP.md`) and `bcaa992b43c2ce14f4b2af28e82d02848751a448` (`ARCHITECTURE.md`).
- Fresh CI on code+documentation head `bcaa992b43c2ce14f4b2af28e82d02848751a448`: Android CI run #151 succeeded; Unit tests, Build debug APK, and Upload debug APK all passed.
- Whole-branch comparison from Phase 1 accepted head to `bcaa992...`: 27 Phase 2 commits, limited to programming/adapters/project resources, tests and Phase 2 docs.
- Review found no changes to legacy `domain/ProgramModels.kt`, runtime execution, 3D, bridge, or hardware-control code.
- Patch scan found no implementation TODO/TBD placeholders; `.sprj` references are classifier/preservation/docs only; no `REAL_HARDWARE` implementation is added.
- No destructive formatter/regenerator or `.sprj` parser was introduced.

## Acceptance cases verified by tests

1. Token concatenation reconstructs source exactly.
2. CRLF/comments survive; apostrophe inside a string is not a comment delimiter.
3. Unknown SPEL+ lines survive as Direct Code.
4. Syntax-invalid edits preserve exact source and the last valid semantic model.
5. Recognized Speed operand editing changes only the owned range.
6. Untouched `.pts` and opaque bytes export identically.
7. Known-preserved/opaque resources cannot be mutated through the editable-resource API.
8. RC+ source adapter opens the same source text through the registry.

## Deliberate deferrals

- Full SPEL+ grammar/expression parser.
- Native RC+ compiler/Build/Run equivalence.
- Local program execution semantics and TaskRuntime/I/O/simulation clock.
- `.pts` semantic parsing/writing.
- `.sprj` internal parsing/writing.
- Encoding auto-detection beyond explicit ASCII-compatible fixtures.
- C4 self-collision (Issue #7).

## Final handoff checkpoint

- Phase 1 remains preserved on `feature/shared-runtime-foundation` and is not merged.
- Phase 2 remains on Draft PR #8 and must not be merged without explicit user instruction.
- Code/docs head verified before this ledger commit: `bcaa992b43c2ce14f4b2af28e82d02848751a448`, CI run #151 success.
- This ledger update is intended as the final file-changing commit for Phase 2. The next action is **only** to verify GitHub Actions on the new PR #8 HEAD. If green, record final acceptance in a PR comment without creating another commit.
- If Codex resumes after final acceptance, it must not redo Phase 2. It should read this ledger and PR #8 comments, then proceed to the next approved phase/plan.
