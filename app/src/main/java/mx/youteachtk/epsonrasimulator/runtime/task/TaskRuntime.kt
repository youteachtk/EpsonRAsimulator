package mx.youteachtk.epsonrasimulator.runtime.task

import mx.youteachtk.epsonrasimulator.runtime.clock.SimulationClock
import mx.youteachtk.epsonrasimulator.runtime.io.IoRuntime

class TaskRuntime(
    private val clock: SimulationClock,
    private val io: IoRuntime
) {
    private data class TaskRecord(
        val id: TaskId,
        val program: TaskProgram,
        var status: TaskStatus,
        var instructionIndex: Int = 0,
        var waitReason: TaskWaitReason? = null,
        val breakpoints: MutableSet<Int>,
        var suppressBreakpointOnce: Int? = null
    )

    private val tasks = linkedMapOf<TaskId, TaskRecord>()

    fun start(
        id: TaskId,
        program: TaskProgram,
        breakpoints: Set<Int> = emptySet()
    ): TaskSnapshot {
        require(id !in tasks) {
            "Task id already exists: ${id.value}"
        }
        require(breakpoints.all { it in program.instructions.indices }) {
            "Breakpoint outside program"
        }

        val task = TaskRecord(
            id = id,
            program = program,
            status = TaskStatus.RUNNING,
            breakpoints = breakpoints.toMutableSet()
        )
        tasks[id] = task
        runUntilBlocked(task)
        return snapshot(id)
    }

    fun snapshot(id: TaskId): TaskSnapshot {
        val task = requireTask(id)
        return TaskSnapshot(
            id = task.id,
            programName = task.program.name,
            status = task.status,
            instructionIndex = task.instructionIndex,
            currentLocation = task.program.instructions
                .getOrNull(task.instructionIndex)
                ?.location,
            waitReason = task.waitReason,
            breakpoints = task.breakpoints.toSet()
        )
    }

    fun setBreakpoint(
        id: TaskId,
        instructionIndex: Int,
        enabled: Boolean = true
    ): TaskSnapshot {
        val task = requireTask(id)
        require(instructionIndex in task.program.instructions.indices) {
            "Breakpoint outside program"
        }

        if (enabled) {
            task.breakpoints += instructionIndex
        } else {
            task.breakpoints -= instructionIndex
        }
        return snapshot(id)
    }

    fun pause(id: TaskId): TaskSnapshot {
        val task = requireTask(id)
        require(!task.status.isTerminal()) {
            "Cannot pause terminal task: ${id.value}"
        }
        task.status = TaskStatus.PAUSED
        return snapshot(id)
    }

    fun resume(id: TaskId): TaskSnapshot {
        val task = requireTask(id)
        require(!task.status.isTerminal()) {
            "Cannot resume terminal task: ${id.value}"
        }

        task.status = TaskStatus.RUNNING
        if (task.instructionIndex in task.breakpoints) {
            task.suppressBreakpointOnce = task.instructionIndex
        }
        runUntilBlocked(task)
        return snapshot(id)
    }

    fun step(id: TaskId): TaskSnapshot {
        val task = requireTask(id)
        require(!task.status.isTerminal()) {
            "Cannot step terminal task: ${id.value}"
        }

        task.status = TaskStatus.RUNNING
        executeCurrent(task)

        if (task.status == TaskStatus.RUNNING) {
            task.status =
                if (task.instructionIndex >= task.program.instructions.size) {
                    TaskStatus.FINISHED
                } else {
                    TaskStatus.PAUSED
                }
        }
        return snapshot(id)
    }

    fun refresh(id: TaskId): TaskSnapshot {
        val task = requireTask(id)
        if (task.status != TaskStatus.WAITING) {
            return snapshot(id)
        }

        task.status = TaskStatus.RUNNING
        executeCurrent(task)
        if (task.status == TaskStatus.RUNNING) {
            runUntilBlocked(task)
        }
        return snapshot(id)
    }

    fun stop(id: TaskId): TaskSnapshot {
        val task = requireTask(id)
        if (!task.status.isTerminal()) {
            task.status = TaskStatus.ABORTED
            task.waitReason = null
        }
        return snapshot(id)
    }

    private fun runUntilBlocked(task: TaskRecord) {
        while (task.status == TaskStatus.RUNNING) {
            if (task.instructionIndex >= task.program.instructions.size) {
                task.status = TaskStatus.FINISHED
                return
            }

            val atBreakpoint = task.instructionIndex in task.breakpoints
            if (
                atBreakpoint &&
                task.suppressBreakpointOnce != task.instructionIndex
            ) {
                task.status = TaskStatus.HALTED
                return
            }

            if (task.suppressBreakpointOnce == task.instructionIndex) {
                task.suppressBreakpointOnce = null
            }

            executeCurrent(task)
        }
    }

    private fun executeCurrent(task: TaskRecord) {
        if (task.instructionIndex >= task.program.instructions.size) {
            task.status = TaskStatus.FINISHED
            return
        }

        when (
            val action =
                task.program.instructions[task.instructionIndex].action
        ) {
            is TaskAction.SetOutput -> {
                io.setOutput(action.index, action.value)
                task.waitReason = null
                task.instructionIndex += 1
            }

            is TaskAction.WaitForInput -> {
                if (io.readInput(action.index) == action.expected) {
                    task.waitReason = null
                    task.instructionIndex += 1
                } else {
                    task.waitReason = TaskWaitReason.Input(
                        index = action.index,
                        expected = action.expected
                    )
                    task.status = TaskStatus.WAITING
                }
            }

            is TaskAction.WaitDuration -> {
                val existingTarget =
                    (task.waitReason as? TaskWaitReason.UntilTime)
                        ?.targetTimeMillis

                val target = existingTarget ?: Math.addExact(
                    clock.state.timeMillis,
                    action.durationMillis
                )

                if (clock.state.timeMillis >= target) {
                    task.waitReason = null
                    task.instructionIndex += 1
                } else {
                    task.waitReason = TaskWaitReason.UntilTime(target)
                    task.status = TaskStatus.WAITING
                }
            }
        }
    }

    private fun requireTask(id: TaskId): TaskRecord =
        requireNotNull(tasks[id]) {
            "Unknown task: ${id.value}"
        }

    private fun TaskStatus.isTerminal(): Boolean =
        this == TaskStatus.FINISHED || this == TaskStatus.ABORTED
}
