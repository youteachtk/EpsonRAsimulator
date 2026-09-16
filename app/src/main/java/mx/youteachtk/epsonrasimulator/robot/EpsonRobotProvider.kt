package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.EpsonRobotCatalog
import mx.youteachtk.epsonrasimulator.domain.RobotDefinition

object EpsonRobotProvider : RobotProvider {
    override val providerId: String = "epson"

    override val robots: List<RobotDefinition> = listOf(
        EpsonRobotCatalog.C4_A601S
    )
}
