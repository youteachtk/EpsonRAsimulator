package mx.youteachtk.epsonrasimulator.programming

import mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelAnalyzer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgramDocumentSessionTest {
    @Test
    fun invalidEditKeepsLastValidSemanticModel() {
        val session = ProgramDocumentSession(
            analyzer = SpelAnalyzer,
            initialSource = "Function main\n  Go P1\nFend\n"
        )
        val valid = session.document.semanticModel

        val invalid = session.replaceSource("Function main\n  Go P1\n")

        assertEquals(ProgramSupportState.SYNTAX_INVALID, invalid.supportState)
        assertNull(invalid.semanticModel)
        assertSame(valid, invalid.lastValidSemanticModel)
        assertEquals("Function main\n  Go P1\n", invalid.sourceText)
        assertTrue(invalid.tokens.isNotEmpty())
    }

    @Test
    fun directCodeMarksDocumentPartiallySupportedWithoutDestroyingSemantics() {
        val session = ProgramDocumentSession(
            analyzer = SpelAnalyzer,
            initialSource = "Function main\n  FutureCommand X\nFend\n"
        )

        assertEquals(
            ProgramSupportState.PARTIALLY_SUPPORTED,
            session.document.supportState
        )
        assertNotNull(session.document.semanticModel)
        assertNotNull(session.document.lastValidSemanticModel)
        assertTrue(session.document.diagnostics.isEmpty())
    }

    @Test
    fun recognizedSubsetMarksDocumentSupported() {
        val session = ProgramDocumentSession(
            analyzer = SpelAnalyzer,
            initialSource = "Function main\n  Speed 50\n  Go P1\nFend\n"
        )

        assertEquals(ProgramSupportState.SUPPORTED, session.document.supportState)
        assertNotNull(session.document.semanticModel)
        assertSame(
            session.document.semanticModel,
            session.document.lastValidSemanticModel
        )
    }

    @Test
    fun validEditReplacesLastValidSemanticModel() {
        val session = ProgramDocumentSession(
            analyzer = SpelAnalyzer,
            initialSource = "Function main\n  Go P1\nFend\n"
        )
        val first = session.document.semanticModel

        val edited = session.replaceSource(
            "Function main\n  Move P2\nFend\n"
        )

        assertEquals(ProgramSupportState.SUPPORTED, edited.supportState)
        assertNotNull(edited.semanticModel)
        assertSame(edited.semanticModel, edited.lastValidSemanticModel)
        assertTrue(first !== edited.semanticModel)
    }
}
