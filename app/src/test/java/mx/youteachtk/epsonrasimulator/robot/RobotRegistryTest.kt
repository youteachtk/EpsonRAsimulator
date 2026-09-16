package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.EpsonRobotCatalog
import mx.youteachtk.epsonrasimulator.domain.JointDefinition
import mx.youteachtk.epsonrasimulator.domain.JointType
import mx.youteachtk.epsonrasimulator.domain.RobotDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class RobotRegistryTest {
    private fun robot(id: String) = RobotDefinition(
        id = id,
        displayName = id,
        joints = listOf(
            JointDefinition(
                id = "J1",
                displayName = "J1",
                type = JointType.REVOLUTE,
                minValue = -180.0,
                maxValue = 180.0
            )
        )
    )

    @Test
    fun findsRobotAcrossProviders() {
        val provider = object : RobotProvider {
            override val providerId = "fake"
            override val robots = listOf(robot("robot-a"))
        }

        val registry = RobotRegistry(listOf(provider))

        assertEquals("robot-a", registry.require("robot-a").id)
        assertNull(registry.find("missing"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDuplicateRobotIdsAcrossProviders() {
        val first = object : RobotProvider {
            override val providerId = "first"
            override val robots = listOf(robot("duplicate"))
        }
        val second = object : RobotProvider {
            override val providerId = "second"
            override val robots = listOf(robot("duplicate"))
        }

        RobotRegistry(listOf(first, second))
    }

    @Test(expected = IllegalArgumentException::class)
    fun requireRejectsUnknownRobotId() {
        RobotRegistry(emptyList()).require("missing")
    }

    @Test
    fun epsonProviderExposesCatalogDefinition() {
        assertEquals("epson", EpsonRobotProvider.providerId)
        assertEquals(listOf(EpsonRobotCatalog.C4_A601S), EpsonRobotProvider.robots)
        assertSame(EpsonRobotCatalog.C4_A601S, EpsonRobotProvider.robots.single())
    }
}
