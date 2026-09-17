package mx.youteachtk.epsonrasimulator.programming

enum class DiagnosticSeverity {
    INFO,
    WARNING,
    ERROR
}

data class ProgramDiagnostic(
    val code: String,
    val message: String,
    val severity: DiagnosticSeverity,
    val range: SourceRange
)
