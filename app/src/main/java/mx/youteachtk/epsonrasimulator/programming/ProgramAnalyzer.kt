package mx.youteachtk.epsonrasimulator.programming

fun interface ProgramAnalyzer {
    fun analyze(
        sourceText: String,
        previousValidSemanticModel: ProgramSemanticModel?
    ): ProgramDocument
}
