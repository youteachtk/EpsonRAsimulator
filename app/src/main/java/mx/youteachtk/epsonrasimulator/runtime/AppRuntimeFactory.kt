package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.adapters.AdapterRegistry
import mx.youteachtk.epsonrasimulator.adapters.rcplus.RcPlus7SimulatorAdapter
import mx.youteachtk.epsonrasimulator.adapters.rcplus.RcPlusProjectFormatAdapter
import mx.youteachtk.epsonrasimulator.adapters.rcplus.SpelPlusLanguageAdapter
import mx.youteachtk.epsonrasimulator.robot.EpsonRobotProvider
import mx.youteachtk.epsonrasimulator.robot.RobotRegistry

data class AppRuntimeBundle(
    val runtime: SharedRuntime,
    val robots: RobotRegistry,
    val adapters: AdapterRegistry
)

object AppRuntimeFactory {
    fun createDefault(): AppRuntimeBundle {
        val robots = RobotRegistry(
            providers = listOf(EpsonRobotProvider)
        )

        val adapters = AdapterRegistry(
            simulators = listOf(RcPlus7SimulatorAdapter),
            languages = listOf(SpelPlusLanguageAdapter),
            projectFormats = listOf(RcPlusProjectFormatAdapter)
        )

        val robot = robots.require("epson-c4-a601s")
        val simulator = adapters.requireSimulator(RcPlus7SimulatorAdapter.id)

        val runtime = SharedRuntime(
            robots = robots,
            initialState = SharedRuntimeState(
                simulatorAdapterId = simulator.id,
                trainingProfileId = simulator.defaultProfileId,
                activeRobotId = robot.id,
                jointState = robot.zeroState(),
                connectionMode = ConnectionMode.LOCAL_SIMULATION
            )
        )

        return AppRuntimeBundle(
            runtime = runtime,
            robots = robots,
            adapters = adapters
        )
    }
}
