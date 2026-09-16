package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapterId
import mx.youteachtk.epsonrasimulator.domain.CartesianPose
import mx.youteachtk.epsonrasimulator.domain.JointState
import mx.youteachtk.epsonrasimulator.domain.TeachPoint
import mx.youteachtk.epsonrasimulator.robot.EpsonRobotProvider
import mx.youteachtk.epsonrasimulator.robot.RobotRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class SharedRuntimeTest {
    private fun runtime(): SharedRuntime {
        val robots = RobotRegistry(listOf(EpsonRobotProvider))
        return SharedRuntime(
            robots = robots,
            initialState = SharedRuntimeState(
                simulatorAdapterId = SimulatorAdapterId("epson-rcplus-7.5.3"),
                trainingProfileId = TrainingProfileId("school-setup"),
                activeRobotId = "epson-c4-a601s",
                jointState = robots.require("epson-c4-a601s").zeroState()
            )
        )
    }

    @Test
    fun jointCommandUsesRobotLimits() {
        val runtime = runtime()

        runtime.dispatch(RuntimeCommand.SetJointValue(index = 0, value = 999.0))

        assertEquals(170.0, runtime.state.jointState[0], 0.0)
    }

    @Test
    fun resetRestoresRobotZeroState() {
        val runtime = runtime()
        runtime.dispatch(RuntimeCommand.SetJointValue(index = 1, value = -40.0))

        runtime.dispatch(RuntimeCommand.ResetJoints)

        assertEquals(runtime.activeRobot().zeroState(), runtime.state.jointState)
    }

    @Test
    fun saveTeachPointUpdatesCanonicalState() {
        val runtime = runtime()
        val point = TeachPoint(
            name = "P1",
            pose = CartesianPose(100.0, 200.0, 300.0)
        )

        runtime.dispatch(RuntimeCommand.SaveTeachPoint(point))

        assertEquals(point, runtime.state.teachPoints["P1"])
    }

    @Test
    fun subscribersReceiveInitialAndChangedState() {
        val runtime = runtime()
        val observed = mutableListOf<SharedRuntimeState>()

        val subscription = runtime.subscribe { observed += it }
        runtime.dispatch(RuntimeCommand.SetJointValue(index = 0, value = 20.0))
        subscription.cancel()

        assertEquals(2, observed.size)
        assertTrue(observed.last().jointState[0] == 20.0)
    }

    @Test
    fun rejectedNonLocalConnectionModeDoesNotMutateState() {
        val runtime = runtime()
        val before = runtime.state

        try {
            runtime.dispatch(
                RuntimeCommand.SetConnectionMode(ConnectionMode.RCPLUS_DIGITAL_TWIN)
            )
            fail("Expected non-local connection mode to be rejected")
        } catch (_: IllegalStateException) {
        }

        assertEquals(before, runtime.state)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsOutOfRangeInitialJointState() {
        val robots = RobotRegistry(listOf(EpsonRobotProvider))

        SharedRuntime(
            robots = robots,
            initialState = SharedRuntimeState(
                simulatorAdapterId = SimulatorAdapterId("epson-rcplus-7.5.3"),
                trainingProfileId = TrainingProfileId("school-setup"),
                activeRobotId = "epson-c4-a601s",
                jointState = JointState(listOf(999.0, 0.0, 0.0, 0.0, 0.0, 0.0))
            )
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNonFiniteJointCommand() {
        val runtime = runtime()

        runtime.dispatch(RuntimeCommand.SetJointValue(index = 0, value = Double.NaN))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsNonFiniteJointStateCommand() {
        val runtime = runtime()

        runtime.dispatch(
            RuntimeCommand.SetJointState(
                listOf(0.0, 0.0, Double.POSITIVE_INFINITY, 0.0, 0.0, 0.0)
            )
        )
    }
}
