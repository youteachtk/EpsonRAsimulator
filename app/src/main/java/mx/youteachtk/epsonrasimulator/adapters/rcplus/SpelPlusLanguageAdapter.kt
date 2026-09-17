package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.ProgrammingLanguageAdapterId
import mx.youteachtk.epsonrasimulator.adapters.SourceProgrammingLanguageAdapter
import mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelAnalyzer
import mx.youteachtk.epsonrasimulator.programming.ProgramDocumentSession

object SpelPlusLanguageAdapter : SourceProgrammingLanguageAdapter {
    override val id = ProgrammingLanguageAdapterId("epson-spel-plus")
    override val displayName = "SPEL+"

    override fun openSession(sourceText: String): ProgramDocumentSession =
        ProgramDocumentSession(
            analyzer = SpelAnalyzer,
            initialSource = sourceText
        )
}
