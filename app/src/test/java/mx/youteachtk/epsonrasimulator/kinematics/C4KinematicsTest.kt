package mx.youteachtk.epsonrasimulator.kinematics

import org.junit.Assert.assertEquals
import org.junit.Test

class C4KinematicsTest {

    @Test
    fun zeroPosePlacesFlangeAtCadDerivedLocation() {
        val tcp = C4Kinematics.tcpCadMm(List(6) { 0.0 })

        assertEquals(0.0, tcp.x, 1e-9)
        assertEquals(570.0, tcp.y, 1e-9)
        assertEquals(-415.0, tcp.z, 1e-9)
    }

    @Test
    fun zeroPoseCandidateFrameIsRightHandedZUpMapping() {
        val tcp = C4Kinematics.tcpRcCandidateMm(List(6) { 0.0 })

        assertEquals(0.0, tcp.x, 1e-9)
        assertEquals(415.0, tcp.y, 1e-9)
        assertEquals(570.0, tcp.z, 1e-9)
    }

    @Test
    fun j6RotationDoesNotMoveFlangeCenter() {
        val zero = C4Kinematics.tcpCadMm(List(6) { 0.0 })
        val j6Rotated = C4Kinematics.tcpCadMm(
            listOf(0.0, 0.0, 0.0, 0.0, 0.0, 90.0)
        )

        assertEquals(zero.x, j6Rotated.x, 1e-9)
        assertEquals(zero.y, j6Rotated.y, 1e-9)
        assertEquals(zero.z, j6Rotated.z, 1e-9)
    }
}
