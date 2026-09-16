package mx.youteachtk.epsonrasimulator.programming

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
