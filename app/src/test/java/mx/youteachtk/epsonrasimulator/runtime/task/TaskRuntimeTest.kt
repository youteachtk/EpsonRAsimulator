package mx.youteachtk.epsonrasimulator.runtime.task

import mx.youteachtk.epsonrasimulator.programming.SourceRange
import mx.youteachtk.epsonrasimulator.runtime.clock.SimulationClock
import mx.youteachtk.epsonrasimulator.runtime.io.IoLayout
import mx.youteachtk.epsonrasimulator.runtime.io.IoRuntime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskRuntimeTest {
    @Test
    fun breakpointStepResumeAndStopUseOneTaskState() {
        val io = IoRuntime(IoLayout(0..3, 0..3))
        val tasks = TaskRuntime(SimulationClock(), io)
        val id = TaskId("main")
        val program = TaskProgram(
            name = "main.prg",
            instructions = listOf(
                TaskInstruction(TaskAction.SetOutput(0, true)),
                TaskInstruction(TaskAction.SetOutput(1, true)),
                TaskInstruction(TaskAction.SetOutput(2, true))
            )
        )

        tasks.start(id, program, breakpoints = setOf(1))

        assertEquals(TaskStatus.HALTED, tasks.snapshot(id).status)
        assertEquals(1, tasks.snapshot(id).instructionIndex)
        assertTrue(io.readOutput(0))
        assertFalse(io.readOutput(1))

        tasks.step(id)

        assertTrue(io.readOutput(1))
        assertEquals(TaskStatus.PAUSED, tasks.snapshot(id).status)
        assertEquals(2, tasks.snapshot(id).instructionIndex)

        tasks.resume(id)

        assertEquals(TaskStatus.FINISHED, tasks.snapshot(id).status)
        assertTrue(io.readOutput(2))
        assertNull(tasks.snapshot(id).currentLocation)

        val stopped = TaskId("stopped")
        tasks.start(stopped, program, breakpoints = setOf(0))
        tasks.stop(stopped)

        assertEquals(TaskStatus.ABORTED, tasks.snapshot(stopped).status)
        assertFalse(io.readOutput(1).not())
    }

    @Test
    fun snapshotExposesCurrentDebugSourceLocationAtBreakpoint() {
        val io = IoRuntime(IoLayout(0..0, 0..0))
        val tasks = TaskRuntime(SimulationClock(), io)
        val location = DebugSourceLocation(
            programName = "main.prg",
            functionName = "main",
            range = SourceRange(10, 20)
        )
        val id = TaskId("debug")

        tasks.start(
            id = id,
            program = TaskProgram(
                "main.prg",
                listOf(
                    TaskInstruction(
                        action = TaskAction.SetOutput(0, true),
                        location = location
                    )
                )
            ),
            breakpoints = setOf(0)
        )

        val snapshot = tasks.snapshot(id)
        assertEquals(TaskStatus.HALTED, snapshot.status)
        assertEquals(location, snapshot.currentLocation)
        assertFalse(io.readOutput(0))
    }

    @Test
    fun pauseAndResumeProgressPastCurrentBreakpoint() {
        val io = IoRuntime(IoLayout(0..0, 0..0))
        val tasks = TaskRuntime(SimulationClock(), io)
        val id = TaskId("pause")
        val program = TaskProgram(
            "pause.prg",
            listOf(TaskInstruction(TaskAction.SetOutput(0, true)))
        )

        tasks.start(id, program, breakpoints = setOf(0))
        tasks.pause(id)

        assertEquals(TaskStatus.PAUSED, tasks.snapshot(id).status)

        tasks.resume(id)

        assertEquals(TaskStatus.FINISHED, tasks.snapshot(id).status)
        assertTrue(io.readOutput(0))
    }

    @Test
    fun breakpointCanBeDisabledBeforeResume() {
        val io = IoRuntime(IoLayout(0..0, 0..0))
        val tasks = TaskRuntime(SimulationClock(), io)
        val id = TaskId("toggle")
        val program = TaskProgram(
            "toggle.prg",
            listOf(TaskInstruction(TaskAction.SetOutput(0, true)))
        )

        tasks.start(id, program, breakpoints = setOf(0))
        tasks.setBreakpoint(id, instructionIndex = 0, enabled = false)
        tasks.resume(id)

        assertEquals(TaskStatus.FINISHED, tasks.snapshot(id).status)
        assertTrue(io.readOutput(0))
    }

    @Test
    fun emptyProgramFinishesImmediately() {
        val tasks = TaskRuntime(
            SimulationClock(),
            IoRuntime(IoLayout.EMPTY)
        )
        val id = TaskId("empty")

        tasks.start(id, TaskProgram("empty.prg", emptyList()))

        assertEquals(TaskStatus.FINISHED, tasks.snapshot(id).status)
        assertEquals(0, tasks.snapshot(id).instructionIndex)
    }

    @Test(expected = IllegalArgumentException::class)
    fun startRejectsBreakpointOutsideProgram() {
        val tasks = TaskRuntime(
            SimulationClock(),
            IoRuntime(IoLayout.EMPTY)
        )

        tasks.start(
            TaskId("invalid-breakpoint"),
            TaskProgram("empty.prg", emptyList()),
            breakpoints = setOf(0)
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun taskIdRejectsBlankValue() {
        TaskId(" ")
    }
}
