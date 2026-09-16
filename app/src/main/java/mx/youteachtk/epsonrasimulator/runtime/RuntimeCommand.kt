package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.domain.TeachPoint

sealed interface RuntimeCommand {
    data class SelectRobot(val robotId: String) : RuntimeCommand
    data class SetJointValue(val index: Int, val value: Double) : RuntimeCommand
    data class SetJointState(val values: List<Double>) : RuntimeCommand
    data object ResetJoints : RuntimeCommand
    data class SaveTeachPoint(val point: TeachPoint) : RuntimeCommand
    data class RemoveTeachPoint(val name: String) : RuntimeCommand
    data class SetConnectionMode(val mode: ConnectionMode) : RuntimeCommand
}
