package mx.youteachtk.epsonrasimulator.domain

sealed interface ProgramAction {
    data class MoveToPoint(val pointName: String) : ProgramAction
    data class SetSpeed(val percent: Int) : ProgramAction
    data class Wait(val milliseconds: Long) : ProgramAction
    data class RunToolCommand(val command: ToolCommand) : ProgramAction
}

data class RobotProgram(
    val name: String,
    val actions: List<ProgramAction>
)
