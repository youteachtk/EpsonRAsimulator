package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import mx.youteachtk.epsonrasimulator.programming.SourceEdit

object SpelSourceEditor {
    fun replaceArgument(
        source: String,
        statement: SpelStatement.Recognized,
        replacement: String
    ): String =
        SourceEdit(
            range = statement.argumentRange,
            replacement = replacement
        ).apply(source)
}
