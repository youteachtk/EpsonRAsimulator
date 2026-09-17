package mx.youteachtk.epsonrasimulator.adapters.rcplus.project

import mx.youteachtk.epsonrasimulator.project.NativeKnownEditable
import mx.youteachtk.epsonrasimulator.project.NativeKnownPreserved
import mx.youteachtk.epsonrasimulator.project.NativeOpaque
import mx.youteachtk.epsonrasimulator.project.NativeResourceKind
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RcPlusResourceClassifierTest {
    private fun classify(path: String) =
        RcPlusResourceClassifier.classify(path, byteArrayOf(1, 2, 3))

    @Test
    fun classifiesVerifiedRcPlusResourceNamesWithoutGuessingContents() {
        assertTrue(classify("main.prg") is NativeKnownEditable)
        assertTrue(classify("common.INC") is NativeKnownEditable)

        assertEquals(
            NativeResourceKind.POINTS,
            (classify("robot.pts") as NativeKnownPreserved).kind
        )
        assertEquals(
            NativeResourceKind.MACRO,
            (classify("setup.mac") as NativeKnownPreserved).kind
        )
        assertEquals(
            NativeResourceKind.IO_LABELS,
            (classify("IOLABEL.DAT") as NativeKnownPreserved).kind
        )
        assertEquals(
            NativeResourceKind.USER_ERRORS,
            (classify("USERERRORS.DAT") as NativeKnownPreserved).kind
        )
        assertEquals(
            NativeResourceKind.PROJECT_DESCRIPTOR,
            (classify("cell.sprj") as NativeKnownPreserved).kind
        )

        assertTrue(classify("vendor.bin") is NativeOpaque)
    }

    @Test
    fun classifierDoesNotRetainMutableCallerByteArray() {
        val original = byteArrayOf(1, 2, 3)
        val resource = RcPlusResourceClassifier.classify("robot.pts", original)

        original[0] = 99

        assertArrayEquals(byteArrayOf(1, 2, 3), resource.bytesCopy())
    }
}
