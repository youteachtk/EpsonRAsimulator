package mx.youteachtk.epsonrasimulator.programming

data class SourceToken(
    val kind: String,
    val text: String,
    val range: SourceRange,
    val trivia: Boolean
)
