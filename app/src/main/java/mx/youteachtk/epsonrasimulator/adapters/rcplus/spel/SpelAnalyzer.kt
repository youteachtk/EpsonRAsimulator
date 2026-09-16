package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import mx.youteachtk.epsonrasimulator.programming.DiagnosticSeverity
import mx.youteachtk.epsonrasimulator.programming.ProgramDiagnostic
import mx.youteachtk.epsonrasimulator.programming.SourceRange
import mx.youteachtk.epsonrasimulator.programming.SourceToken

data class SpelAnalysisResult(
    val semanticModel: SpelProgramSemanticModel?,
    val diagnostics: List<ProgramDiagnostic>
)

object SpelAnalyzer {
    fun analyze(source: String): SpelAnalysisResult {
        val diagnostics = mutableListOf<ProgramDiagnostic>()
        val functions = mutableListOf<SpelFunction>()
        val topLevelDirectCode = mutableListOf<SpelStatement.DirectCode>()
        var currentFunction: MutableFunction? = null

        fun error(code: String, message: String, range: SourceRange) {
            diagnostics += ProgramDiagnostic(
                code = code,
                message = message,
                severity = DiagnosticSeverity.ERROR,
                range = range
            )
        }

        splitLines(source, SpelLexer.lex(source)).forEach { line ->
            val significant = line.tokens.filterNot { it.trivia }
            if (significant.isEmpty()) {
                return@forEach
            }

            val first = significant.first()
            val keyword = if (first.kind == SpelTokenKind.IDENTIFIER.name) {
                first.text.lowercase()
            } else {
                null
            }

            when (keyword) {
                "function" -> {
                    if (currentFunction != null) {
                        error(
                            code = "SPEL_NESTED_FUNCTION",
                            message = "Nested Function declarations are not supported by the local analyzer.",
                            range = first.range
                        )
                        return@forEach
                    }

                    val nameToken = significant.getOrNull(1)
                    if (
                        nameToken == null ||
                        nameToken.kind != SpelTokenKind.IDENTIFIER.name
                    ) {
                        error(
                            code = "SPEL_FUNCTION_NAME_REQUIRED",
                            message = "Function requires an identifier.",
                            range = first.range
                        )
                        return@forEach
                    }

                    currentFunction = MutableFunction(
                        name = nameToken.text,
                        nameRange = nameToken.range,
                        start = first.range.start
                    )
                }

                "fend" -> {
                    val function = currentFunction
                    if (function == null) {
                        error(
                            code = "SPEL_FEND_WITHOUT_FUNCTION",
                            message = "Fend does not have a matching Function.",
                            range = first.range
                        )
                    } else {
                        functions += SpelFunction(
                            name = function.name,
                            nameRange = function.nameRange,
                            sourceRange = SourceRange(
                                function.start,
                                first.range.endExclusive
                            ),
                            statements = function.statements.toList()
                        )
                        currentFunction = null
                    }
                }

                "call", "go", "move", "speed", "wait" -> {
                    val function = currentFunction
                    if (function == null) {
                        topLevelDirectCode += directCode(source, significant)
                        return@forEach
                    }

                    val argumentTokens = significant.drop(1)
                    if (argumentTokens.isEmpty()) {
                        error(
                            code = "SPEL_OPERAND_REQUIRED",
                            message = first.text + " requires an operand.",
                            range = first.range
                        )
                        return@forEach
                    }

                    val argumentRange = SourceRange(
                        argumentTokens.first().range.start,
                        argumentTokens.last().range.endExclusive
                    )
                    val statementRange = SourceRange(
                        first.range.start,
                        argumentTokens.last().range.endExclusive
                    )
                    val argumentText = source.substring(
                        argumentRange.start,
                        argumentRange.endExclusive
                    )

                    function.statements += recognizedStatement(
                        keyword = keyword,
                        argumentText = argumentText,
                        argumentRange = argumentRange,
                        sourceRange = statementRange
                    )
                }

                else -> {
                    val directCode = directCode(source, significant)
                    val function = currentFunction
                    if (function == null) {
                        topLevelDirectCode += directCode
                    } else {
                        function.statements += directCode
                    }
                }
            }
        }

        currentFunction?.let { function ->
            error(
                code = "SPEL_FUNCTION_UNCLOSED",
                message = "Function " + function.name + " is missing Fend.",
                range = SourceRange(function.start, function.nameRange.endExclusive)
            )
        }

        val hasErrors = diagnostics.any { it.severity == DiagnosticSeverity.ERROR }
        val semanticModel = if (hasErrors) {
            null
        } else {
            SpelProgramSemanticModel(
                functions = functions.toList(),
                topLevelDirectCode = topLevelDirectCode.toList()
            )
        }

        return SpelAnalysisResult(
            semanticModel = semanticModel,
            diagnostics = diagnostics.toList()
        )
    }

    private fun recognizedStatement(
        keyword: String,
        argumentText: String,
        argumentRange: SourceRange,
        sourceRange: SourceRange
    ): SpelStatement.Recognized =
        when (keyword) {
            "call" -> SpelStatement.Call(argumentText, argumentRange, sourceRange)
            "go" -> SpelStatement.Go(argumentText, argumentRange, sourceRange)
            "move" -> SpelStatement.Move(argumentText, argumentRange, sourceRange)
            "speed" -> SpelStatement.Speed(argumentText, argumentRange, sourceRange)
            "wait" -> SpelStatement.Wait(argumentText, argumentRange, sourceRange)
            else -> error("Unsupported recognized SPEL keyword: " + keyword)
        }

    private fun directCode(
        source: String,
        significant: List<SourceToken>
    ): SpelStatement.DirectCode {
        val range = SourceRange(
            significant.first().range.start,
            significant.last().range.endExclusive
        )
        return SpelStatement.DirectCode(
            sourceText = source.substring(range.start, range.endExclusive),
            sourceRange = range
        )
    }

    private fun splitLines(
        source: String,
        tokens: List<SourceToken>
    ): List<SourceLine> {
        if (source.isEmpty()) {
            return emptyList()
        }

        val lines = mutableListOf<SourceLine>()
        val current = mutableListOf<SourceToken>()
        var lineStart = 0

        tokens.forEach { token ->
            if (token.kind == SpelTokenKind.NEWLINE.name) {
                lines += SourceLine(
                    tokens = current.toList(),
                    start = lineStart,
                    endExclusive = token.range.start
                )
                current.clear()
                lineStart = token.range.endExclusive
            } else {
                current += token
            }
        }

        if (current.isNotEmpty() || lineStart < source.length) {
            lines += SourceLine(
                tokens = current.toList(),
                start = lineStart,
                endExclusive = source.length
            )
        }

        return lines
    }

    private data class SourceLine(
        val tokens: List<SourceToken>,
        val start: Int,
        val endExclusive: Int
    )

    private data class MutableFunction(
        val name: String,
        val nameRange: SourceRange,
        val start: Int,
        val statements: MutableList<SpelStatement> = mutableListOf()
    )
}
