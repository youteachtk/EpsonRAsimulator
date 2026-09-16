package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import mx.youteachtk.epsonrasimulator.programming.SourceEdit
import mx.youteachtk.epsonrasimulator.programming.SourceRange
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpelSourceEditorTest {
    @Test
    fun replacingSpeedArgumentPreservesWhitespaceCommentAndOpaqueCodeExactly() {
        val source =
            "Function main\r\n" +
                "  Speed   50   ' keep this\r\n" +
                "  FutureCommand  A, B\r\n" +
                "Fend\r\n"

        val document = SpelAnalyzer.analyze(source, null)
        val speed = (document.semanticModel as SpelProgramSemanticModel)
            .functions.single().statements
            .filterIsInstance<SpelStatement.Speed>()
            .single()

        val edited = SpelSourceEditor.replaceArgument(source, speed, "75")

        assertEquals(
            "Function main\r\n" +
                "  Speed   75   ' keep this\r\n" +
                "  FutureCommand  A, B\r\n" +
                "Fend\r\n",
            edited
        )

        val reparsed = SpelAnalyzer.analyze(edited, null)
        val updatedSpeed = (reparsed.semanticModel as SpelProgramSemanticModel)
            .functions.single().statements
            .filterIsInstance<SpelStatement.Speed>()
            .single()

        assertTrue(reparsed.diagnostics.isEmpty())
        assertEquals("75", updatedSpeed.argumentText)
    }

    @Test(expected = IllegalArgumentException::class)
    fun sourceEditRejectsRangeOutsideSource() {
        SourceEdit(SourceRange(0, 50), "x").apply("short")
    }
}
