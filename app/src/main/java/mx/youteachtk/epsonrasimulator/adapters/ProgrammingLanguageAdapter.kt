package mx.youteachtk.epsonrasimulator.adapters

import mx.youteachtk.epsonrasimulator.programming.ProgramDocumentSession

interface ProgrammingLanguageAdapter {
    val id: ProgrammingLanguageAdapterId
    val displayName: String
}

interface SourceProgrammingLanguageAdapter : ProgrammingLanguageAdapter {
    fun openSession(sourceText: String): ProgramDocumentSession
}
