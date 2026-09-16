package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpelLexerTest {
    @Test
    fun lexingIsCharacterLossless() {
        val source =
            "Function main\r\n" +
                "  Speed 50  ' fast\r\n" +
                "  Print \"don't change\"\n" +
                "#unknown @ x\n" +
                "Fend\n"

        val tokens = SpelLexer.lex(source)

        assertEquals(source, tokens.joinToString(separator = "") { it.text })
        assertEquals(0, tokens.first().range.start)
        assertEquals(source.length, tokens.last().range.endExclusive)

        tokens.zipWithNext().forEach { (left, right) ->
            assertEquals(left.range.endExclusive, right.range.start)
        }
    }

    @Test
    fun apostropheInsideStringIsNotCommentStart() {
        val source = "Print \"don't\" ' comment\n"

        val tokens = SpelLexer.lex(source)

        val string = tokens.single { it.kind == SpelTokenKind.STRING.name }
        val comment = tokens.single { it.kind == SpelTokenKind.COMMENT.name }

        assertEquals("\"don't\"", string.text)
        assertEquals("' comment", comment.text)
        assertTrue(comment.trivia)
    }
}
