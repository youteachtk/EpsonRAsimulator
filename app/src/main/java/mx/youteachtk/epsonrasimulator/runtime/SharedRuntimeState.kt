package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapterId
import mx.youteachtk.epsonrasimulator.domain.JointState
import mx.youteachtk.epsonrasimulator.domain.TeachPoint

data class SharedRuntimeState(
    val simulatorAdapterId: SimulatorAdapterId,
    val trainingProfileId: TrainingProfileId,
    val activeRobotId: String,
    val jointState: JointState,
    val teachPoints: Map<String, TeachPoint> = emptyMap(),
    val connectionMode: ConnectionMode = ConnectionMode.LOCAL_SIMULATION
)
