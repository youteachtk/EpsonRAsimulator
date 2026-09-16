package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.RobotDefinition

interface RobotProvider {
    val providerId: String
    val robots: List<RobotDefinition>
}
