package mx.youteachtk.epsonrasimulator.runtime.io

data class IoLayout(
    val inputRange: IntRange,
    val outputRange: IntRange
) {
    init {
        require(inputRange.isEmpty() || inputRange.first >= 0) {
            "Input range must be non-negative"
        }
        require(outputRange.isEmpty() || outputRange.first >= 0) {
            "Output range must be non-negative"
        }
    }

    companion object {
        val EMPTY = IoLayout(
            inputRange = 0..-1,
            outputRange = 0..-1
        )
    }
}

data class IoSnapshot(
    val inputs: Map<Int, Boolean>,
    val outputs: Map<Int, Boolean>,
    val inputLabels: Map<Int, String>,
    val outputLabels: Map<Int, String>
)

class IoRuntime(
    val layout: IoLayout
) {
    private val inputValues = mutableMapOf<Int, Boolean>()
    private val outputValues = mutableMapOf<Int, Boolean>()
    private val inputLabels = mutableMapOf<Int, String>()
    private val outputLabels = mutableMapOf<Int, String>()

    fun readInput(index: Int): Boolean {
        requireInput(index)
        return inputValues[index] ?: false
    }

    fun readOutput(index: Int): Boolean {
        requireOutput(index)
        return outputValues[index] ?: false
    }

    fun setInput(index: Int, value: Boolean) {
        requireInput(index)
        inputValues[index] = value
    }

    fun setOutput(index: Int, value: Boolean) {
        requireOutput(index)
        outputValues[index] = value
    }

    fun setInputLabel(index: Int, label: String?) {
        requireInput(index)
        if (label == null) {
            inputLabels.remove(index)
        } else {
            inputLabels[index] = label
        }
    }

    fun setOutputLabel(index: Int, label: String?) {
        requireOutput(index)
        if (label == null) {
            outputLabels.remove(index)
        } else {
            outputLabels[index] = label
        }
    }

    fun snapshot(): IoSnapshot =
        IoSnapshot(
            inputs = layout.inputRange.associateWith { inputValues[it] ?: false },
            outputs = layout.outputRange.associateWith { outputValues[it] ?: false },
            inputLabels = inputLabels.toMap(),
            outputLabels = outputLabels.toMap()
        )

    private fun requireInput(index: Int) {
        require(index in layout.inputRange) {
            "Input out of range: $index"
        }
    }

    private fun requireOutput(index: Int) {
        require(index in layout.outputRange) {
            "Output out of range: $index"
        }
    }
}
