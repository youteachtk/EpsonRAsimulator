package mx.youteachtk.epsonrasimulator.adapters.rcplus.spel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SpelAnalyzerTest {
    @Test
    fun recognizesVerifiedSubsetAndPreservesUnknownAsDirectCode() {
        val source = """
            Function main
              Speed 50
              Go P1
              VendorSpecific Foo(1)
              Move P2
              Wait Sw(1)
              Call Finish
            Fend
        """.trimIndent()

        val result = SpelAnalyzer.analyze(source)

        assertTrue(result.diagnostics.isEmpty())
        val model = assertNotNull(result.semanticModel) as SpelProgramSemanticModel
        val body = model.functions.single().statements

        assertTrue(body[0] is SpelStatement.Speed)
        assertTrue(body[1] is SpelStatement.Go)
        assertTrue(body[2] is SpelStatement.DirectCode)
        assertTrue(body[3] is SpelStatement.Move)
        assertTrue(body[4] is SpelStatement.Wait)
        assertTrue(body[5] is SpelStatement.Call)

        assertEquals("50", (body[0] as SpelStatement.Speed).argumentText)
        assertEquals("P1", (body[1] as SpelStatement.Go).argumentText)
        assertEquals("VendorSpecific Foo(1)", (body[2] as SpelStatement.DirectCode).sourceText)
        assertEquals("P2", (body[3] as SpelStatement.Move).argumentText)
        assertEquals("Sw(1)", (body[4] as SpelStatement.Wait).argumentText)
        assertEquals("Finish", (body[5] as SpelStatement.Call).argumentText)
    }

    @Test
    fun missingFendProducesLocalStructuralDiagnostic() {
        val result = SpelAnalyzer.analyze("Function main\n  Go P1\n")

        assertEquals("SPEL_FUNCTION_UNCLOSED", result.diagnostics.single().code)
        assertEquals(DiagnosticSeverity.ERROR, result.diagnostics.single().severity)
        assertNull(result.semanticModel)
    }

    @Test
    fun unknownStatementAloneIsNotAParserError() {
        val result = SpelAnalyzer.analyze(
            "Function main\n  FutureCommand X\nFend\n"
        )

        assertTrue(result.diagnostics.isEmpty())
        val model = assertNotNull(result.semanticModel) as SpelProgramSemanticModel
        assertTrue(
            model.functions.single().statements.single() is SpelStatement.DirectCode
        )
    }

    @Test
    fun recognizedOperandRangeExcludesTrailingWhitespaceAndComment() {
        val source = "Function main\r\n  Speed   50   ' keep\r\nFend\r\n"

        val result = SpelAnalyzer.analyze(source)

        val model = assertNotNull(result.semanticModel) as SpelProgramSemanticModel
        val speed = model.functions.single().statements.single() as SpelStatement.Speed

        assertEquals("50", source.substring(speed.argumentRange.start, speed.argumentRange.endExclusive))
    }

    @Test
    fun structuralErrorsRejectSemanticModel() {
        val cases = listOf(
            "Fend\n" to "SPEL_FEND_WITHOUT_FUNCTION",
            "Function\nFend\n" to "SPEL_FUNCTION_NAME_REQUIRED",
            "Function outer\nFunction inner\nFend\nFend\n" to "SPEL_NESTED_FUNCTION",
            "Function main\n  Go   ' missing\nFend\n" to "SPEL_OPERAND_REQUIRED"
        )

        cases.forEach { (source, expectedCode) ->
            val result = SpelAnalyzer.analyze(source)
            assertTrue(
                "Expected diagnostic $expectedCode for source: $source",
                result.diagnostics.any { it.code == expectedCode }
            )
            assertNull(result.semanticModel)
        }
    }
}
