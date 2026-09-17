package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import mx.youteachtk.epsonrasimulator.programming.SourceRange
import mx.youteachtk.epsonrasimulator.programming.SourceToken

object SpelLexer {
    fun lex(source: String): List<SourceToken> {
        if (source.isEmpty()) {
            return emptyList()
        }

        val tokens = mutableListOf<SourceToken>()
        var index = 0

        fun emit(
            kind: SpelTokenKind,
            start: Int,
            endExclusive: Int,
            trivia: Boolean
        ) {
            tokens += SourceToken(
                kind = kind.name,
                text = source.substring(start, endExclusive),
                range = SourceRange(start, endExclusive),
                trivia = trivia
            )
        }

        while (index < source.length) {
            val start = index
            val current = source[index]

            when {
                current == '\r' || current == '\n' -> {
                    if (
                        current == '\r' &&
                        index + 1 < source.length &&
                        source[index + 1] == '\n'
                    ) {
                        index += 2
                    } else {
                        index += 1
                    }
                    emit(SpelTokenKind.NEWLINE, start, index, trivia = true)
                }

                current.isWhitespace() -> {
                    index += 1
                    while (
                        index < source.length &&
                        source[index] != '\r' &&
                        source[index] != '\n' &&
                        source[index].isWhitespace()
                    ) {
                        index += 1
                    }
                    emit(SpelTokenKind.WHITESPACE, start, index, trivia = true)
                }

                current == '\'' -> {
                    index += 1
                    while (
                        index < source.length &&
                        source[index] != '\r' &&
                        source[index] != '\n'
                    ) {
                        index += 1
                    }
                    emit(SpelTokenKind.COMMENT, start, index, trivia = true)
                }

                current == '"' -> {
                    index += 1
                    while (index < source.length) {
                        if (source[index] == '"') {
                            index += 1
                            break
                        }
                        index += 1
                    }
                    emit(SpelTokenKind.STRING, start, index, trivia = false)
                }

                current.isLetter() || current == '_' -> {
                    index += 1
                    while (
                        index < source.length &&
                        (
                            source[index].isLetterOrDigit() ||
                                source[index] == '_' ||
                                source[index] == '$'
                        )
                    ) {
                        index += 1
                    }
                    emit(SpelTokenKind.IDENTIFIER, start, index, trivia = false)
                }

                current.isDigit() -> {
                    index += 1
                    while (
                        index < source.length &&
                        (source[index].isDigit() || source[index] == '.')
                    ) {
                        index += 1
                    }
                    emit(SpelTokenKind.NUMBER, start, index, trivia = false)
                }

                current == ',' -> {
                    index += 1
                    emit(SpelTokenKind.COMMA, start, index, trivia = false)
                }

                current == '(' -> {
                    index += 1
                    emit(SpelTokenKind.LPAREN, start, index, trivia = false)
                }

                current == ')' -> {
                    index += 1
                    emit(SpelTokenKind.RPAREN, start, index, trivia = false)
                }

                else -> {
                    index += 1
                    emit(SpelTokenKind.SYMBOL, start, index, trivia = false)
                }
            }
        }

        return tokens
    }
}
