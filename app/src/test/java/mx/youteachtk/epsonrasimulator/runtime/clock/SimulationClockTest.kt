package mx.youteachtk.epsonrasimulator.runtime.clock

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SimulationClockTest {
    @Test
    fun pausedClockOnlyMovesWhenExplicitlyStepped() {
        val clock = SimulationClock()

        clock.advanceBy(500)
        assertEquals(0L, clock.state.timeMillis)

        clock.stepBy(250)
        assertEquals(250L, clock.state.timeMillis)
        assertFalse(clock.state.running)
    }

    @Test
    fun runningClockAppliesConfiguredSpeedScaleDeterministically() {
        val clock = SimulationClock()
        clock.setSpeedScale(2.0)
        clock.start()

        clock.advanceBy(250)

        assertEquals(500L, clock.state.timeMillis)
        assertTrue(clock.state.running)
    }

    @Test
    fun pauseStopsScaledAdvancementWithoutChangingTime() {
        val clock = SimulationClock()
        clock.start()
        clock.advanceBy(100)
        clock.pause()
        clock.advanceBy(100)

        assertEquals(100L, clock.state.timeMillis)
        assertFalse(clock.state.running)
    }

    @Test(expected = IllegalArgumentException::class)
    fun clockRejectsNonPositiveSpeedScale() {
        SimulationClock().setSpeedScale(0.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun clockRejectsNegativeAdvance() {
        SimulationClock().advanceBy(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun clockRejectsNegativeStep() {
        SimulationClock().stepBy(-1)
    }
}
