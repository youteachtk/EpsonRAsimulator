package mx.youteachtk.epsonrasimulator.programming

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SourceRangeTest {
    @Test
    fun sourceRangeUsesHalfOpenOffsets() {
        val range = SourceRange(2, 5)

        assertEquals(3, range.length)
        assertTrue(range.contains(2))
        assertTrue(range.contains(4))
        assertFalse(range.contains(5))
    }

    @Test(expected = IllegalArgumentException::class)
    fun sourceRangeRejectsReverseOffsets() {
        SourceRange(5, 2)
    }
}
