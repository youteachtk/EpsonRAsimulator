package mx.youteachtk.epsonrasimulator.runtime

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapabilityModelsTest {
    @Test
    fun capabilitySetChecksSingleAndMultipleRequirements() {
        val robotManager = CapabilityId("robot-manager")
        val ioMonitor = CapabilityId("io-monitor")
        val set = CapabilitySet(setOf(robotManager, ioMonitor))

        assertTrue(robotManager in set)
        assertTrue(set.containsAll(setOf(robotManager, ioMonitor)))
        assertFalse(set.containsAll(setOf(CapabilityId("vision"))))
    }
}
