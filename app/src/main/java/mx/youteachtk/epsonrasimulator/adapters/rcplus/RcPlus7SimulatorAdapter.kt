package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapter
import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapterId
import mx.youteachtk.epsonrasimulator.runtime.CapabilitySet
import mx.youteachtk.epsonrasimulator.runtime.TrainingProfileId

object RcPlus7SimulatorAdapter : SimulatorAdapter {
    override val id = SimulatorAdapterId("epson-rcplus-7.5.3")
    override val displayName = "EPSON RC+ 7.0 v7.5.3"
    override val programmingLanguageId = SpelPlusLanguageAdapter.id
    override val projectFormatId = RcPlusProjectFormatAdapter.id
    override val defaultProfileId = TrainingProfileId("school-setup")
    override val capabilities = CapabilitySet(
        setOf(
            RcPlusCapabilities.PROJECT_EXPLORER,
            RcPlusCapabilities.ROBOT_MANAGER,
            RcPlusCapabilities.COMMAND_WINDOW,
            RcPlusCapabilities.IO_MONITOR,
            RcPlusCapabilities.TASK_MANAGER,
            RcPlusCapabilities.BUILD_RUN_STATUS
        )
    )
}
