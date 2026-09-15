package mx.youteachtk.epsonrasimulator.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EpsonRobotCatalogTest {

    private val robot = EpsonRobotCatalog.C4_A601S

    @Test
    fun c4HasSixRevoluteJoints() {
        assertEquals(6, robot.joints.size)
        assertTrue(robot.joints.all { it.type == JointType.REVOLUTE })
    }

    @Test
    fun documentedZeroPoseIsWithinEveryJointRange() {
        robot.joints.forEachIndexed { index, joint ->
            assertTrue(joint.contains(robot.zeroJointValues[index]))
        }
    }

    @Test
    fun validatedStateClampsValuesToDocumentedRanges() {
        val state = robot.validatedState(
            listOf(-999.0, 999.0, -999.0, 999.0, -999.0, 999.0)
        )

        assertEquals(-170.0, state[0], 0.0)
        assertEquals(65.0, state[1], 0.0)
        assertEquals(-51.0, state[2], 0.0)
        assertEquals(200.0, state[3], 0.0)
        assertEquals(-135.0, state[4], 0.0)
        assertEquals(360.0, state[5], 0.0)
    }

    @Test
    fun c4ReachAndPayloadMatchCatalog() {
        assertEquals(600.0, robot.reachMm ?: 0.0, 0.0)
        assertEquals(665.0, robot.wristFlangeReachMm ?: 0.0, 0.0)
        assertEquals(1.0, robot.ratedPayloadKg ?: 0.0, 0.0)
        assertEquals(4.0, robot.maxPayloadKg ?: 0.0, 0.0)
    }
}
