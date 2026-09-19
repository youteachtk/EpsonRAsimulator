package mx.youteachtk.epsonrasimulator.runtime.clock

import kotlin.math.round

data class SimulationClockState(
    val timeMillis: Long = 0L,
    val running: Boolean = false,
    val speedScale: Double = 1.0
)

class SimulationClock {
    var state: SimulationClockState = SimulationClockState()
        private set

    fun start() {
        state = state.copy(running = true)
    }

    fun pause() {
        state = state.copy(running = false)
    }

    fun setSpeedScale(scale: Double) {
        require(scale.isFinite() && scale > 0.0) {
            "Simulation speed scale must be finite and greater than zero"
        }
        state = state.copy(speedScale = scale)
    }

    fun advanceBy(realMillis: Long) {
        require(realMillis >= 0L) {
            "Real-time advance must be non-negative"
        }
        if (!state.running) {
            return
        }

        val scaledMillis = round(realMillis * state.speedScale).toLong()
        state = state.copy(
            timeMillis = Math.addExact(state.timeMillis, scaledMillis)
        )
    }

    fun stepBy(simulationMillis: Long) {
        require(simulationMillis >= 0L) {
            "Simulation step must be non-negative"
        }
        state = state.copy(
            timeMillis = Math.addExact(state.timeMillis, simulationMillis)
        )
    }
}
