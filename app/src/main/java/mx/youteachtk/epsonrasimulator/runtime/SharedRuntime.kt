package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.domain.RobotDefinition
import mx.youteachtk.epsonrasimulator.robot.RobotRegistry

class SharedRuntime(
    private val robots: RobotRegistry,
    initialState: SharedRuntimeState
) {
    private val listeners = linkedSetOf<(SharedRuntimeState) -> Unit>()

    var state: SharedRuntimeState = initialState
        private set

    init {
        val robot = robots.require(initialState.activeRobotId)
        validateInitialJointState(robot, initialState)
        check(initialState.connectionMode == ConnectionMode.LOCAL_SIMULATION) {
            "Only Local Simulation is executable in this phase"
        }
    }

    fun activeRobot(): RobotDefinition = robots.require(state.activeRobotId)

    fun dispatch(command: RuntimeCommand): SharedRuntimeState {
        val current = state
        val next = when (command) {
            is RuntimeCommand.SelectRobot -> {
                val robot = robots.require(command.robotId)
                current.copy(
                    activeRobotId = robot.id,
                    jointState = robot.zeroState()
                )
            }

            is RuntimeCommand.SetJointValue -> {
                val robot = activeRobot()
                require(command.index in robot.joints.indices) {
                    "Joint index out of range: ${command.index}"
                }
                require(command.value.isFinite()) {
                    "Joint value must be finite"
                }
                val values = current.jointState.values.toMutableList()
                values[command.index] = robot.joints[command.index].clamp(command.value)
                current.copy(jointState = robot.validatedState(values))
            }

            is RuntimeCommand.SetJointState -> {
                requireFinite(command.values)
                current.copy(jointState = activeRobot().validatedState(command.values))
            }

            RuntimeCommand.ResetJoints ->
                current.copy(jointState = activeRobot().zeroState())

            is RuntimeCommand.SaveTeachPoint ->
                current.copy(
                    teachPoints = current.teachPoints + (command.point.name to command.point)
                )

            is RuntimeCommand.RemoveTeachPoint ->
                current.copy(teachPoints = current.teachPoints - command.name)

            is RuntimeCommand.SetConnectionMode -> {
                check(command.mode == ConnectionMode.LOCAL_SIMULATION) {
                    "Only Local Simulation is executable in this phase"
                }
                current.copy(connectionMode = command.mode)
            }
        }

        if (next != current) {
            state = next
            listeners.toList().forEach { it(next) }
        }

        return state
    }

    fun subscribe(listener: (SharedRuntimeState) -> Unit): RuntimeSubscription {
        listeners += listener
        listener(state)
        return RuntimeSubscription { listeners -= listener }
    }

    private fun validateInitialJointState(
        robot: RobotDefinition,
        initialState: SharedRuntimeState
    ) {
        require(initialState.jointState.values.size == robot.joints.size) {
            "Initial joint state does not match active robot"
        }
        initialState.jointState.values.forEachIndexed { index, value ->
            require(value.isFinite()) {
                "Initial joint value must be finite"
            }
            require(robot.joints[index].contains(value)) {
                "Initial joint value for ${robot.joints[index].id} is outside its configured range"
            }
        }
    }

    private fun requireFinite(values: List<Double>) {
        require(values.all(Double::isFinite)) {
            "Joint values must be finite"
        }
    }
}

class RuntimeSubscription(
    private val onCancel: () -> Unit
) {
    private var cancelled = false

    fun cancel() {
        if (!cancelled) {
            cancelled = true
            onCancel()
        }
    }
}
