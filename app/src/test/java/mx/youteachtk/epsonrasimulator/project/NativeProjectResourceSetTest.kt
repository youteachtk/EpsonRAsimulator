package mx.youteachtk.epsonrasimulator.project

import mx.youteachtk.epsonrasimulator.adapters.rcplus.SpelPlusLanguageAdapter
import mx.youteachtk.epsonrasimulator.adapters.rcplus.project.RcPlusResourceClassifier
import mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelProgramSemanticModel
import mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelSourceEditor
import mx.youteachtk.epsonrasimulator.adapters.rcplus.spel.SpelStatement
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class NativeProjectResourceSetTest {
    @Test
    fun untouchedKnownAndOpaqueResourcesRoundTripByteForByte() {
        val files = linkedMapOf(
            "main.prg" to "Function main\r\n  Speed 50\r\nFend\r\n".toByteArray(Charsets.US_ASCII),
            "common.inc" to "#define X 1\r\n".toByteArray(Charsets.US_ASCII),
            "robot.pts" to byteArrayOf(0x01, 0x02, 0x7f),
            "vendor.bin" to byteArrayOf(0x00, 0x10, 0x20)
        )

        val resources = NativeProjectResourceSet.import(files, RcPlusResourceClassifier)
        val exported = resources.export()

        files.forEach { (path, expected) ->
            assertArrayEquals(expected, exported.getValue(path))
        }
    }

    @Test
    fun supportedProgramEditChangesOnlyProgramBytes() {
        val originalProgram =
            "Function main\r\n" +
                "  Speed   50   ' preserve\r\n" +
                "  FutureCommand X\r\n" +
                "Fend\r\n"

        val files = linkedMapOf(
            "main.prg" to originalProgram.toByteArray(Charsets.US_ASCII),
            "robot.pts" to byteArrayOf(9, 8, 7),
            "vendor.bin" to byteArrayOf(6, 5, 4)
        )

        val resources = NativeProjectResourceSet.import(files, RcPlusResourceClassifier)
        val session = SpelPlusLanguageAdapter.openSession(originalProgram)
        val speed = (session.document.semanticModel as SpelProgramSemanticModel)
            .functions.single()
            .statements
            .filterIsInstance<SpelStatement.Speed>()
            .single()
        val editedText = SpelSourceEditor.replaceArgument(originalProgram, speed, "75")

        resources.replaceEditable(
            "main.prg",
            editedText.toByteArray(Charsets.US_ASCII)
        )

        val exported = resources.export()
        assertEquals(
            "Function main\r\n  Speed   75   ' preserve\r\n  FutureCommand X\r\nFend\r\n",
            exported.getValue("main.prg").toString(Charsets.US_ASCII)
        )
        assertArrayEquals(byteArrayOf(9, 8, 7), exported.getValue("robot.pts"))
        assertArrayEquals(byteArrayOf(6, 5, 4), exported.getValue("vendor.bin"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotReplaceKnownPreservedResourceThroughEditablePath() {
        val resources = NativeProjectResourceSet.import(
            mapOf("robot.pts" to byteArrayOf(1)),
            RcPlusResourceClassifier
        )

        resources.replaceEditable("robot.pts", byteArrayOf(2))
    }

    @Test
    fun exportedBytesAreDefensiveCopies() {
        val resources = NativeProjectResourceSet.import(
            mapOf("vendor.bin" to byteArrayOf(1, 2, 3)),
            RcPlusResourceClassifier
        )

        val firstExport = resources.export()
        firstExport.getValue("vendor.bin")[0] = 99

        assertArrayEquals(
            byteArrayOf(1, 2, 3),
            resources.export().getValue("vendor.bin")
        )
    }
}
