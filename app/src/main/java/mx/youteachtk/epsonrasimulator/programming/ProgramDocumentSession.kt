package mx.youteachtk.epsonrasimulator.programming

class ProgramDocumentSession(
    private val analyzer: ProgramAnalyzer,
    initialSource: String
) {
    var document: ProgramDocument = analyzer.analyze(
        sourceText = initialSource,
        previousValidSemanticModel = null
    )
        private set

    fun replaceSource(newSource: String): ProgramDocument {
        val previousValid =
            document.semanticModel ?: document.lastValidSemanticModel

        document = analyzer.analyze(
            sourceText = newSource,
            previousValidSemanticModel = previousValid
        )
        return document
    }
}
