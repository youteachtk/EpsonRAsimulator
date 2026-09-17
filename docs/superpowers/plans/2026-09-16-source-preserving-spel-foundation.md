# Source-Preserving SPEL+ + Native Project Resource Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a lossless SPEL+ source-document foundation and RC+ native-resource abstraction that can preserve untouched source/comments/unknown code exactly while exposing a deliberately small verified semantic subset.

**Architecture:** Native source remains authoritative. A lossless lexer preserves every source character as concrete tokens, a conservative line-oriented SPEL+ analyzer recognizes only verified constructs, and unknown regions remain Direct Code. ProgramDocument retains the last valid semantic model across syntax-invalid edits. Native project resources are classified without attempting to parse undocumented .sprj/.pts internals, and unchanged binary resources round-trip byte-for-byte.

**Tech Stack:** Kotlin/JVM 17, JUnit 4.13.2, Android Gradle Plugin 9.4.0, Gradle 9.6, existing single Android app module.

**Spec:** `docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`

## Global Constraints

- Base this phase on accepted Phase 1 HEAD `8a9a0770f7522bd2ae58b053b796d51403258c1e`.
- Work only on `feature/source-preserving-spel-foundation`; do not modify `feature/shared-runtime-foundation`.
- Keep SceneView pinned at `4.35.0`; this phase has no 3D dependency changes.
- Native source text is authoritative for preservation.
- Do not destructively regenerate unsupported source.
- Do not parse or rewrite opaque/undocumented `.sprj` internals.
- Unknown SPEL+ lines are Direct Code, not syntax errors merely because the local parser does not understand them.
- Parser diagnostics created locally must never masquerade as Epson/RC+ native error numbers/messages.
- No program execution, TaskRuntime, I/O runtime, workcell execution, bridge behavior, or physical robot control in this phase.
- Existing `domain/ProgramModels.kt` is legacy educational scaffolding; do not expand it into the source-preserving model and do not delete it in this phase.
- Initial verified SPEL+ semantic subset: `Function...Fend`, `Call`, `Go`, `Move`, `Speed`, `Wait`.
- The lexer must preserve comments introduced by apostrophe outside strings, whitespace, newlines, strings, punctuation and unrecognized characters exactly.
- `.prg` and `.inc` are classified as known editable text resources; `.pts`, `.mac`, `.sprj`, `IOLABEL.DAT`, and `USERERRORS.DAT` are known-preserved until their exact write semantics are separately implemented; unknown files are opaque.
- Project-resource byte arrays must be defensively copied at import/export boundaries.
- TDD for all pure Kotlin behavior.
- Final automated gate: `gradle testDebugUnitTest --stacktrace` and `gradle assembleDebug --stacktrace`.
- Keep the Phase 2 PR Draft; no merge without explicit user instruction.

## File Structure

### Neutral programming core
- `programming/SourceRange.kt` — immutable half-open source ranges.
- `programming/SourceToken.kt` — lossless concrete token representation.
- `programming/ProgramSemanticModel.kt` — marker contract for language semantic models.
- `programming/ProgramDiagnostic.kt` — local diagnostics and severity.
- `programming/ProgramDocument.kt` — source/tokens/semantic/last-valid/support state.
- `programming/ProgramAnalyzer.kt` — neutral analyzer contract.
- `programming/ProgramDocumentSession.kt` — current document + last-valid semantic retention.
- `programming/SourceEdit.kt` — safe half-open text replacement primitive.

### SPEL+ adapter implementation
- `adapters/rcplus/spel/SpelTokenKind.kt` — SPEL+ concrete token categories.
- `adapters/rcplus/spel/SpelLexer.kt` — character-lossless lexer.
- `adapters/rcplus/spel/SpelSemanticModel.kt` — small verified semantic subset + Direct Code.
- `adapters/rcplus/spel/SpelAnalyzer.kt` — conservative line-oriented analyzer and structural diagnostics.
- `adapters/rcplus/spel/SpelSourceEditor.kt` — range-preserving edits to recognized statement arguments.

### Adapter wiring
- Modify `adapters/ProgrammingLanguageAdapter.kt` — add source-capable sub-interface without breaking metadata-only adapters.
- Modify `adapters/AdapterRegistry.kt` — resolve a source-capable language explicitly.
- Modify `adapters/rcplus/SpelPlusLanguageAdapter.kt` — implement source-capable adapter.

### Native project resources
- `project/NativeProjectResource.kt` — four resource categories and native kinds.
- `adapters/rcplus/project/RcPlusResourceClassifier.kt` — RC+ filename/extension classifier.
- `project/NativeProjectResourceSet.kt` — import/export/replace-known-editable with defensive byte copies.

### Tests
- `programming/SourceRangeTest.kt`
- `adapters/rcplus/spel/SpelLexerTest.kt`
- `adapters/rcplus/spel/SpelAnalyzerTest.kt`
- `programming/ProgramDocumentSessionTest.kt`
- `adapters/rcplus/spel/SpelSourceEditorTest.kt`
- update `adapters/AdapterRegistryTest.kt`
- `adapters/rcplus/project/RcPlusResourceClassifierTest.kt`
- `project/NativeProjectResourceSetTest.kt`

---

### Task 1: Source ranges and lossless SPEL+ lexer

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/SourceRange.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/SourceToken.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelTokenKind.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelLexer.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/programming/SourceRangeTest.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelLexerTest.kt`

**Interfaces:**
- Produces: `SourceRange(start: Int, endExclusive: Int)`
- Produces: `SourceToken(kind: String, text: String, range: SourceRange, trivia: Boolean)`
- Produces: `SpelLexer.lex(source: String): List<SourceToken>`
- Guarantee: `tokens.joinToString("") { it.text } == source`

- [ ] **Step 1: Write failing SourceRange tests**

```kotlin
@Test
fun sourceRangeUsesHalfOpenOffsets() {
    val range = SourceRange(2, 5)
    assertEquals(3, range.length)
    assertTrue(range.contains(2))
    assertTrue(range.contains(4))
    assertFalse(range.contains(5))
}

@Test(expected = IllegalArgumentException::class)
fun sourceRangeRejectsReverseOffsets() {
    SourceRange(5, 2)
}
```

- [ ] **Step 2: Run focused test and verify RED**

```bash
gradle :app:testDebugUnitTest --tests mx.youteachtk.epsonrasimulator.programming.SourceRangeTest --stacktrace
```

Expected: compile failure because `SourceRange` does not exist.

- [ ] **Step 3: Implement SourceRange and SourceToken**

```kotlin
package mx.youteachtk.epsonrasimulator.programming

data class SourceRange(
    val start: Int,
    val endExclusive: Int
) {
    init {
        require(start >= 0)
        require(endExclusive >= start)
    }

    val length: Int get() = endExclusive - start

    fun contains(offset: Int): Boolean =
        offset >= start && offset < endExclusive
}

data class SourceToken(
    val kind: String,
    val text: String,
    val range: SourceRange,
    val trivia: Boolean
)
```

- [ ] **Step 4: Write failing lexer preservation tests**

```kotlin
@Test
fun lexingIsCharacterLossless() {
    val source = "Function main\r\n  Speed 50  ' fast\r\n  Print \"don't change\"\n#unknown @ x\nFend\n"
    val tokens = SpelLexer.lex(source)

    assertEquals(source, tokens.joinToString(separator = "") { it.text })
    assertEquals(0, tokens.first().range.start)
    assertEquals(source.length, tokens.last().range.endExclusive)
}

@Test
fun apostropheInsideStringIsNotCommentStart() {
    val source = "Print \"don't\" ' comment\n"
    val tokens = SpelLexer.lex(source)

    val string = tokens.single { it.kind == SpelTokenKind.STRING.name }
    val comment = tokens.single { it.kind == SpelTokenKind.COMMENT.name }
    assertEquals("\"don't\"", string.text)
    assertEquals("' comment", comment.text)
}
```

- [ ] **Step 5: Run lexer test and verify RED**

```bash
gradle :app:testDebugUnitTest --tests mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelLexerTest --stacktrace
```

Expected: compile failure because lexer types do not exist.

- [ ] **Step 6: Implement minimal character-lossless lexer**

```kotlin
enum class SpelTokenKind {
    WHITESPACE,
    NEWLINE,
    COMMENT,
    IDENTIFIER,
    NUMBER,
    STRING,
    COMMA,
    LPAREN,
    RPAREN,
    SYMBOL
}
```

`SpelLexer.lex` must emit every source character exactly once. Apostrophe starts a comment only outside a string. Unknown characters become `SYMBOL`, never discarded.

- [ ] **Step 7: Run focused tests and verify GREEN**

Expected: PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/programming app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel app/src/test/java/mx/youteachtk/epsonrasimulator/programming app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel
git commit -m "feat: add lossless SPEL source lexer"
```

---

### Task 2: Conservative SPEL+ semantic model and analyzer

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/ProgramSemanticModel.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/ProgramDiagnostic.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelSemanticModel.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelAnalyzer.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelAnalyzerTest.kt`

**Interfaces:**
- `interface ProgramSemanticModel`
- `ProgramDiagnostic(code, message, severity, range)`
- `SpelProgramSemanticModel(functions, topLevelDirectCode)`
- Recognized statements: Function/Fend structure, Call, Go, Move, Speed, Wait
- Unrecognized statements: `SpelStatement.DirectCode`

- [ ] **Step 1: Write failing semantic-subset test**

```kotlin
@Test
fun recognizesVerifiedSubsetAndPreservesUnknownAsDirectCode() {
    val source = """
        Function main
          Speed 50
          Go P1
          VendorSpecific Foo(1)
          Move P2
          Wait Sw(1)
          Call Finish
        Fend
    """.trimIndent()

    val result = SpelAnalyzer.analyze(source)

    assertTrue(result.diagnostics.isEmpty())
    val body = result.semanticModel!!.functions.single().statements
    assertTrue(body[0] is SpelStatement.Speed)
    assertTrue(body[1] is SpelStatement.Go)
    assertTrue(body[2] is SpelStatement.DirectCode)
    assertTrue(body[3] is SpelStatement.Move)
    assertTrue(body[4] is SpelStatement.Wait)
    assertTrue(body[5] is SpelStatement.Call)
}
```

- [ ] **Step 2: Write failing structural diagnostics tests**

```kotlin
@Test
fun missingFendProducesLocalStructuralDiagnostic() {
    val result = SpelAnalyzer.analyze("Function main\n  Go P1\n")
    assertEquals("SPEL_FUNCTION_UNCLOSED", result.diagnostics.single().code)
    assertNull(result.semanticModel)
}

@Test
fun unknownStatementAloneIsNotAParserError() {
    val result = SpelAnalyzer.analyze("Function main\n  FutureCommand X\nFend\n")
    assertTrue(result.diagnostics.isEmpty())
    assertTrue(result.semanticModel!!.functions.single().statements.single() is SpelStatement.DirectCode)
}
```

- [ ] **Step 3: Run analyzer tests and verify RED**

```bash
gradle :app:testDebugUnitTest --tests mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelAnalyzerTest --stacktrace
```

Expected: compile failure because semantic/analyzer types do not exist.

- [ ] **Step 4: Implement semantic types**

```kotlin
interface ProgramSemanticModel

enum class DiagnosticSeverity { INFO, WARNING, ERROR }

data class ProgramDiagnostic(
    val code: String,
    val message: String,
    val severity: DiagnosticSeverity,
    val range: SourceRange
)

data class SpelProgramSemanticModel(
    val functions: List<SpelFunction>,
    val topLevelDirectCode: List<SpelStatement.DirectCode>
) : ProgramSemanticModel
```

`SpelStatement` must contain `Call`, `Go`, `Move`, `Speed`, `Wait`, and `DirectCode`. Each recognized operand stores its exact `SourceRange`.

- [ ] **Step 5: Implement conservative line analyzer**

Rules:
- keyword matching is case-insensitive but original text is never normalized;
- comments/trailing whitespace stay outside semantic operand ranges;
- `Function` requires a following identifier;
- `Fend` closes the current function;
- nested `Function`, stray `Fend`, missing function name, and missing required operand produce local `ERROR` diagnostics;
- any other nonblank, noncomment line becomes `DirectCode`;
- structural errors return `semanticModel = null`;
- Direct Code is preserved, not claimed native-valid.

- [ ] **Step 6: Run analyzer tests and verify GREEN**

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/programming app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel
git commit -m "feat: add conservative SPEL semantic analyzer"
```

---

### Task 3: ProgramDocument and last-valid semantic retention

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/ProgramDocument.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/ProgramAnalyzer.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/ProgramDocumentSession.kt`
- Modify: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelAnalyzer.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/programming/ProgramDocumentSessionTest.kt`

**Interfaces:**
- `ProgramSupportState { SUPPORTED, PARTIALLY_SUPPORTED, NATIVE_VALID_NOT_LOCALLY_SIMULATABLE, SYNTAX_INVALID }`
- `ProgramDocument(sourceText, tokens, semanticModel, lastValidSemanticModel, diagnostics, supportState)`
- `ProgramAnalyzer.analyze(sourceText, previousValidSemanticModel): ProgramDocument`
- `ProgramDocumentSession.replaceSource(newSource): ProgramDocument`

- [ ] **Step 1: Write failing retention test**

```kotlin
@Test
fun invalidEditKeepsLastValidSemanticModel() {
    val session = ProgramDocumentSession(
        analyzer = SpelAnalyzer,
        initialSource = "Function main\n  Go P1\nFend\n"
    )
    val valid = session.document.semanticModel
    val invalid = session.replaceSource("Function main\n  Go P1\n")

    assertEquals(ProgramSupportState.SYNTAX_INVALID, invalid.supportState)
    assertNull(invalid.semanticModel)
    assertSame(valid, invalid.lastValidSemanticModel)
    assertEquals("Function main\n  Go P1\n", invalid.sourceText)
}
```

- [ ] **Step 2: Write failing partial-support test**

```kotlin
@Test
fun directCodeMarksDocumentPartiallySupportedWithoutDestroyingSemantics() {
    val session = ProgramDocumentSession(
        analyzer = SpelAnalyzer,
        initialSource = "Function main\n  FutureCommand X\nFend\n"
    )

    assertEquals(ProgramSupportState.PARTIALLY_SUPPORTED, session.document.supportState)
    assertNotNull(session.document.semanticModel)
    assertTrue(session.document.diagnostics.isEmpty())
}
```

- [ ] **Step 3: Run focused test and verify RED**

Expected: compile failure because ProgramDocument APIs do not exist.

- [ ] **Step 4: Implement document/session contracts**

```kotlin
enum class ProgramSupportState {
    SUPPORTED,
    PARTIALLY_SUPPORTED,
    NATIVE_VALID_NOT_LOCALLY_SIMULATABLE,
    SYNTAX_INVALID
}

data class ProgramDocument(
    val sourceText: String,
    val tokens: List<SourceToken>,
    val semanticModel: ProgramSemanticModel?,
    val lastValidSemanticModel: ProgramSemanticModel?,
    val diagnostics: List<ProgramDiagnostic>,
    val supportState: ProgramSupportState
)

fun interface ProgramAnalyzer {
    fun analyze(
        sourceText: String,
        previousValidSemanticModel: ProgramSemanticModel?
    ): ProgramDocument
}
```

`ProgramDocumentSession` initializes via the analyzer and, on each `replaceSource`, passes the current semantic model or previous last-valid semantic model back to the analyzer.

- [ ] **Step 5: Adapt SpelAnalyzer to ProgramAnalyzer**

Support-state rules:
- structural error => `SYNTAX_INVALID`;
- no error + DirectCode => `PARTIALLY_SUPPORTED`;
- no error + only recognized subset => `SUPPORTED`;
- never auto-assign `NATIVE_VALID_NOT_LOCALLY_SIMULATABLE` without external/native validation evidence.

- [ ] **Step 6: Run focused tests and verify GREEN**

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/programming app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel app/src/test/java/mx/youteachtk/epsonrasimulator/programming
git commit -m "feat: preserve last valid SPEL semantics"
```

---

### Task 4: Source-preserving semantic operand edits

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/programming/SourceEdit.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelSourceEditor.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelSourceEditorTest.kt`

**Interfaces:**
- `SourceEdit(range: SourceRange, replacement: String)`
- `SourceEdit.apply(source: String): String`
- `SpelSourceEditor.replaceArgument(source, statement, replacement): String`

- [ ] **Step 1: Write failing untouched-text preservation test**

```kotlin
@Test
fun replacingSpeedArgumentPreservesWhitespaceCommentAndOpaqueCodeExactly() {
    val source =
        "Function main\r\n" +
        "  Speed   50   ' keep this\r\n" +
        "  FutureCommand  A, B\r\n" +
        "Fend\r\n"

    val document = SpelAnalyzer.analyze(source, null)
    val speed = (document.semanticModel as SpelProgramSemanticModel)
        .functions.single().statements.filterIsInstance<SpelStatement.Speed>().single()

    val edited = SpelSourceEditor.replaceArgument(source, speed, "75")

    assertEquals(
        "Function main\r\n" +
        "  Speed   75   ' keep this\r\n" +
        "  FutureCommand  A, B\r\n" +
        "Fend\r\n",
        edited
    )
}
```

- [ ] **Step 2: Write failing range-validation test**

```kotlin
@Test(expected = IllegalArgumentException::class)
fun sourceEditRejectsRangeOutsideSource() {
    SourceEdit(SourceRange(0, 50), "x").apply("short")
}
```

- [ ] **Step 3: Run focused tests and verify RED**

Expected: compile failure because editor types do not exist.

- [ ] **Step 4: Implement SourceEdit and SpelSourceEditor**

```kotlin
data class SourceEdit(
    val range: SourceRange,
    val replacement: String
) {
    fun apply(source: String): String {
        require(range.endExclusive <= source.length)
        return source.substring(0, range.start) +
            replacement +
            source.substring(range.endExclusive)
    }
}
```

`SpelSourceEditor.replaceArgument` accepts only recognized statement types exposing `argumentRange`; DirectCode is never rewritten by semantic helpers.

- [ ] **Step 5: Reparse edited source and assert semantic update**

Re-analysis must see `Speed.argumentText == "75"` with no diagnostics.

- [ ] **Step 6: Run focused tests and verify GREEN**

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/programming/SourceEdit.kt app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelSourceEditor.kt app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/spel/SpelSourceEditorTest.kt
git commit -m "feat: add source-preserving SPEL edits"
```

---

### Task 5: Wire source-capable SPEL+ through the language adapter registry

**Files:**
- Modify: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/ProgrammingLanguageAdapter.kt`
- Modify: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/AdapterRegistry.kt`
- Modify: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/SpelPlusLanguageAdapter.kt`
- Modify/Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/AdapterRegistryTest.kt`

**Interfaces:**
- Add `SourceProgrammingLanguageAdapter`
- Add `AdapterRegistry.sourceLanguageFor(simulatorId): SourceProgrammingLanguageAdapter`
- `SpelPlusLanguageAdapter.openSession(sourceText): ProgramDocumentSession`

- [ ] **Step 1: Write failing registry test**

```kotlin
@Test
fun rcPlusSourceLanguageOpensLosslessSpelDocument() {
    val registry = AdapterRegistry(
        simulators = listOf(RcPlus7SimulatorAdapter),
        languages = listOf(SpelPlusLanguageAdapter),
        projectFormats = listOf(RcPlusProjectFormatAdapter)
    )

    val source = "Function main\n  Go P1\nFend\n"
    val session = registry
        .sourceLanguageFor(RcPlus7SimulatorAdapter.id)
        .openSession(source)

    assertEquals(source, session.document.sourceText)
    assertEquals(ProgramSupportState.SUPPORTED, session.document.supportState)
}
```

- [ ] **Step 2: Run focused test and verify RED**

Expected: compile failure because source-capable adapter API does not exist.

- [ ] **Step 3: Add source-capable sub-interface**

```kotlin
interface ProgrammingLanguageAdapter {
    val id: ProgrammingLanguageAdapterId
    val displayName: String
}

interface SourceProgrammingLanguageAdapter : ProgrammingLanguageAdapter {
    fun openSession(sourceText: String): ProgramDocumentSession
}
```

- [ ] **Step 4: Implement registry resolver**

```kotlin
fun sourceLanguageFor(id: SimulatorAdapterId): SourceProgrammingLanguageAdapter {
    val language = languageFor(id)
    require(language is SourceProgrammingLanguageAdapter) {
        "Language adapter " + language.id.value + " does not provide source-document support"
    }
    return language
}
```

- [ ] **Step 5: Make SpelPlusLanguageAdapter source-capable**

```kotlin
object SpelPlusLanguageAdapter : SourceProgrammingLanguageAdapter {
    override val id = ProgrammingLanguageAdapterId("epson-spel-plus")
    override val displayName = "SPEL+"

    override fun openSession(sourceText: String): ProgramDocumentSession =
        ProgramDocumentSession(
            analyzer = SpelAnalyzer,
            initialSource = sourceText
        )
}
```

- [ ] **Step 6: Run adapter + programming tests and verify GREEN**

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/adapters app/src/test/java/mx/youteachtk/epsonrasimulator/adapters
git commit -m "feat: expose source-capable SPEL adapter"
```

---

### Task 6: Native RC+ project-resource categories and classifier

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/project/NativeProjectResource.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/project/RcPlusResourceClassifier.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/project/RcPlusResourceClassifierTest.kt`

**Interfaces:**
- Resource categories: `NativeKnownEditable`, `NativeKnownPreserved`, `NativeOpaque`, `AppSidecarMetadata`
- Native kinds: PROGRAM, INCLUDE, POINTS, MACRO, IO_LABELS, USER_ERRORS, PROJECT_DESCRIPTOR, UNKNOWN
- `RcPlusResourceClassifier.classify(path, bytes): ProjectResource`

- [ ] **Step 1: Write failing classification tests**

```kotlin
@Test
fun classifiesVerifiedRcPlusResourceNamesWithoutGuessingContents() {
    assertTrue(classify("main.prg") is NativeKnownEditable)
    assertTrue(classify("common.INC") is NativeKnownEditable)
    assertEquals(NativeResourceKind.POINTS, (classify("robot.pts") as NativeKnownPreserved).kind)
    assertEquals(NativeResourceKind.MACRO, (classify("setup.mac") as NativeKnownPreserved).kind)
    assertEquals(NativeResourceKind.IO_LABELS, (classify("IOLABEL.DAT") as NativeKnownPreserved).kind)
    assertEquals(NativeResourceKind.USER_ERRORS, (classify("USERERRORS.DAT") as NativeKnownPreserved).kind)
    assertEquals(NativeResourceKind.PROJECT_DESCRIPTOR, (classify("cell.sprj") as NativeKnownPreserved).kind)
    assertTrue(classify("vendor.bin") is NativeOpaque)
}
```

- [ ] **Step 2: Write failing defensive-copy test**

```kotlin
@Test
fun classifierDoesNotRetainMutableCallerByteArray() {
    val original = byteArrayOf(1, 2, 3)
    val resource = RcPlusResourceClassifier.classify("robot.pts", original)
    original[0] = 99
    assertArrayEquals(byteArrayOf(1, 2, 3), resource.bytesCopy())
}
```

- [ ] **Step 3: Run focused tests and verify RED**

Expected: compile failure because project-resource types do not exist.

- [ ] **Step 4: Implement resource model**

```kotlin
enum class NativeResourceKind {
    PROGRAM,
    INCLUDE,
    POINTS,
    MACRO,
    IO_LABELS,
    USER_ERRORS,
    PROJECT_DESCRIPTOR,
    UNKNOWN
}

sealed interface ProjectResource {
    val path: String
    fun bytesCopy(): ByteArray
}
```

Each concrete resource stores a private `bytes.copyOf()` and returns `copyOf()` from `bytesCopy()`.

- [ ] **Step 5: Implement case-insensitive RC+ classifier**

Rules:
- `.prg` => PROGRAM / editable
- `.inc` => INCLUDE / editable
- `.pts` => POINTS / preserved
- `.mac` => MACRO / preserved
- exact basename `IOLABEL.DAT` => IO_LABELS / preserved
- exact basename `USERERRORS.DAT` => USER_ERRORS / preserved
- `.sprj` => PROJECT_DESCRIPTOR / preserved
- everything else => opaque
- classifier never returns AppSidecarMetadata.

- [ ] **Step 6: Run focused tests and verify GREEN**

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/project app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/project app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/project
git commit -m "feat: classify native RC+ project resources"
```

---

### Task 7: Native resource-set byte preservation and supported .prg edit round-trip

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/project/NativeProjectResourceSet.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/project/NativeProjectResourceSetTest.kt`

**Interfaces:**
- `NativeProjectResourceSet.import(files, classifier)`
- `resource(path)`
- `replaceEditable(path, replacementBytes)`
- `export(): Map<String, ByteArray>`

- [ ] **Step 1: Write failing untouched-byte round-trip test**

```kotlin
@Test
fun untouchedKnownAndOpaqueResourcesRoundTripByteForByte() {
    val files = linkedMapOf(
        "main.prg" to "Function main\r\n  Speed 50\r\nFend\r\n".toByteArray(Charsets.US_ASCII),
        "common.inc" to "#define X 1\r\n".toByteArray(Charsets.US_ASCII),
        "robot.pts" to byteArrayOf(0x01, 0x02, 0x7f),
        "vendor.bin" to byteArrayOf(0x00, 0x10, 0x20)
    )

    val set = NativeProjectResourceSet.import(files, RcPlusResourceClassifier)
    val exported = set.export()

    files.forEach { (path, expected) ->
        assertArrayEquals(expected, exported.getValue(path))
    }
}
```

- [ ] **Step 2: Write failing supported .prg edit test**

```kotlin
@Test
fun supportedProgramEditChangesOnlyProgramBytes() {
    val originalProgram =
        "Function main\r\n" +
        "  Speed   50   ' preserve\r\n" +
        "  FutureCommand X\r\n" +
        "Fend\r\n"

    val files = linkedMapOf(
        "main.prg" to originalProgram.toByteArray(Charsets.US_ASCII),
        "robot.pts" to byteArrayOf(9, 8, 7),
        "vendor.bin" to byteArrayOf(6, 5, 4)
    )

    val resources = NativeProjectResourceSet.import(files, RcPlusResourceClassifier)
    val session = SpelPlusLanguageAdapter.openSession(originalProgram)
    val speed = (session.document.semanticModel as SpelProgramSemanticModel)
        .functions.single().statements.filterIsInstance<SpelStatement.Speed>().single()
    val editedText = SpelSourceEditor.replaceArgument(originalProgram, speed, "75")

    resources.replaceEditable("main.prg", editedText.toByteArray(Charsets.US_ASCII))

    val exported = resources.export()
    assertEquals(
        "Function main\r\n  Speed   75   ' preserve\r\n  FutureCommand X\r\nFend\r\n",
        exported.getValue("main.prg").toString(Charsets.US_ASCII)
    )
    assertArrayEquals(byteArrayOf(9, 8, 7), exported.getValue("robot.pts"))
    assertArrayEquals(byteArrayOf(6, 5, 4), exported.getValue("vendor.bin"))
}
```

- [ ] **Step 3: Write failing protected-resource replacement test**

```kotlin
@Test(expected = IllegalArgumentException::class)
fun cannotReplaceKnownPreservedResourceThroughEditablePath() {
    val resources = NativeProjectResourceSet.import(
        mapOf("robot.pts" to byteArrayOf(1)),
        RcPlusResourceClassifier
    )
    resources.replaceEditable("robot.pts", byteArrayOf(2))
}
```

- [ ] **Step 4: Run focused tests and verify RED**

Expected: compile failure because `NativeProjectResourceSet` does not exist.

- [ ] **Step 5: Implement resource set**

Requirements:
- preserve path identity exactly;
- deep-copy imported bytes;
- `resource(path)` exposes only defensive byte copies;
- `replaceEditable` accepts only `NativeKnownEditable`;
- replacement preserves path/kind;
- `export` deep-copies every byte array;
- no auto-generated sidecar files.

- [ ] **Step 6: Run focused tests and verify GREEN**

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/project app/src/test/java/mx/youteachtk/epsonrasimulator/project
git commit -m "feat: preserve native project resources round trip"
```

---

### Task 8: Documentation and final verification

**Files:**
- Modify: `docs/ARCHITECTURE.md`
- Modify: `docs/ROADMAP.md`
- Create/update: `docs/superpowers/progress/2026-09-16-spel-source-foundation.md`

- [ ] **Step 1: Document implemented facts only**

Record lossless source tokenization, conservative SPEL semantics, Direct Code preservation, last-valid semantic retention, source-capable SPEL adapter, native resource categories and byte-preserving resource set.

Explicitly state that no SPEL execution/build/compiler emulation exists yet and `.pts`/`.sprj` contents are preserved rather than semantically rewritten.

- [ ] **Step 2: Run full unit suite**

```bash
gradle testDebugUnitTest --stacktrace
```

Expected: BUILD SUCCESSFUL with zero failures.

- [ ] **Step 3: Build APK**

```bash
gradle assembleDebug --stacktrace
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Run Git hygiene checks**

```bash
git diff --check
git status --short
```

Expected: no whitespace errors and no uncommitted production/test changes after final commit.

- [ ] **Step 5: Verify acceptance cases**

Focused tests must prove:
1. token concatenation exactly reconstructs source;
2. comments and CRLF survive;
3. apostrophe inside string does not become comment;
4. unknown SPEL line survives as Direct Code;
5. syntax-invalid edit preserves exact source and last valid semantic model;
6. replacing recognized Speed operand changes only operand range;
7. untouched `.pts` and opaque bytes export identically;
8. protected resources cannot be edited through known-editable API.

- [ ] **Step 6: Final whole-branch review**

Review Phase 1 accepted HEAD through Phase 2 HEAD for:
- no destructive formatter/regenerator;
- no deletion/expansion of legacy `domain/ProgramModels.kt`;
- no program execution claims;
- no `.sprj` parser;
- no native error-number impersonation;
- no physical-control behavior;
- no unverified semantics outside declared subset.

- [ ] **Step 7: Update ledger and Draft PR**

Record final commit SHAs, RED/GREEN evidence, review findings/fixes, final CI, unresolved items and exact next phase. Do not merge.

## Self-Review

### Spec coverage
- Native source preservation: Tasks 1–4.
- Token/trivia/concrete syntax: Task 1.
- Small verified semantic subset: Task 2.
- Direct Code preservation: Tasks 2 and 4.
- Syntax-invalid source + last-valid semantics: Task 3.
- ProgrammingLanguageAdapter ownership: Task 5.
- Native resource categories: Task 6.
- Unknown/opaque preservation: Tasks 6–7.
- Import/edit/export preservation exit criterion: Task 7.
- No execution/bridge/hardware scope creep: Global Constraints and Task 8.

### Deliberate deferrals
- Full SPEL+ grammar/expression parsing.
- Native RC+ compiler/build equivalence.
- Local execution semantics.
- `.pts` semantic parsing/writing.
- `.sprj` internal parsing/writing.
- Encoding auto-detection beyond explicit ASCII-compatible fixtures.
- TaskRuntime/I/O/simulation clock (Phase 3).
- Self-collision Issue #7 (motion/workcell diagnostics track).

### Placeholder scan
No TBD/TODO implementation placeholders are present. Deferrals are explicit scope boundaries.

### Type consistency
The plan consistently uses `SourceRange`, `SourceToken`, `ProgramSemanticModel`, `ProgramDocument`, `ProgramDocumentSession`, `SpelProgramSemanticModel`, `SpelStatement`, `SourceProgrammingLanguageAdapter`, `ProjectResource`, and `NativeProjectResourceSet`.
