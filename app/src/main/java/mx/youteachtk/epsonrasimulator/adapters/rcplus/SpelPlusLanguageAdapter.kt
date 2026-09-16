package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.ProgrammingLanguageAdapter
import mx.youteachtk.epsonrasimulator.adapters.ProgrammingLanguageAdapterId

object SpelPlusLanguageAdapter : ProgrammingLanguageAdapter {
    override val id = ProgrammingLanguageAdapterId("epson-spel-plus")
    override val displayName = "SPEL+"
}
