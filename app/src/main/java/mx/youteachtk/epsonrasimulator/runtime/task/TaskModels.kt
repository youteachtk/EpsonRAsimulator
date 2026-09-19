package mx.youteachtk.epsonrasimulator.runtime.task

import mx.youteachtk.epsonrasimulator.programming.SourceRange

@JvmInline
value class TaskId(val value: String) {
    init {
        require(value.isNotBlank()) {
            "Task id must not be blank"
        }
    }
}

enum class TaskStatus {
    RUNNING,
    WAITING,
    HALTED,
    PAUSED,
    FINISHED,
    ABORTED
}

data class DebugSourceLocation(
    val programName: String,
    val functionName: String? = null,
    val range: SourceRange? = null
)

sealed interface TaskAction {
    data class SetOutput(
        val index: Int,
        val value: Boolean
    ) : TaskAction

    data class WaitForInput(
        val index: Int,
        val expected: Boolean = true
    ) : TaskAction

    data class WaitDuration(
        val durationMillis: Long
    ) : TaskAction {
        init {
            require(durationMillis >= 0L) {
                "Wait duration must be non-negative"
            }
        }
    }
}

data class TaskInstruction(
    val action: TaskAction,
    val location: DebugSourceLocation? = null
)

data class TaskProgram(
    val name: String,
    val instructions: List<TaskInstruction>
) {
    init {
        require(name.isNotBlank()) {
            "Program name must not be blank"
        }
    }
}

sealed interface TaskWaitReason {
    data class Input(
        val index: Int,
        val expected: Boolean
    ) : TaskWaitReason

    data class UntilTime(
        val targetTimeMillis: Long
    ) : TaskWaitReason
}

data class TaskSnapshot(
    val id: TaskId,
    val programName: String,
    val status: TaskStatus,
    val instructionIndex: Int,
    val currentLocation: DebugSourceLocation?,
    val waitReason: TaskWaitReason?,
    val breakpoints: Set<Int>
)
