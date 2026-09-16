package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import mx.youteachtk.epsonrasimulator.programming.ProgramSemanticModel
import mx.youteachtk.epsonrasimulator.programming.SourceRange

data class SpelProgramSemanticModel(
    val functions: List<SpelFunction>,
    val topLevelDirectCode: List<SpelStatement.DirectCode>
) : ProgramSemanticModel

data class SpelFunction(
    val name: String,
    val nameRange: SourceRange,
    val sourceRange: SourceRange,
    val statements: List<SpelStatement>
)

sealed interface SpelStatement {
    val sourceRange: SourceRange

    sealed interface Recognized : SpelStatement {
        val argumentText: String
        val argumentRange: SourceRange
    }

    data class Call(
        override val argumentText: String,
        override val argumentRange: SourceRange,
        override val sourceRange: SourceRange
    ) : Recognized

    data class Go(
        override val argumentText: String,
        override val argumentRange: SourceRange,
        override val sourceRange: SourceRange
    ) : Recognized

    data class Move(
        override val argumentText: String,
        override val argumentRange: SourceRange,
        override val sourceRange: SourceRange
    ) : Recognized

    data class Speed(
        override val argumentText: String,
        override val argumentRange: SourceRange,
        override val sourceRange: SourceRange
    ) : Recognized

    data class Wait(
        override val argumentText: String,
        override val argumentRange: SourceRange,
        override val sourceRange: SourceRange
    ) : Recognized

    data class DirectCode(
        val sourceText: String,
        override val sourceRange: SourceRange
    ) : SpelStatement
}
