package mx.youteachtk.epsonrasimulator.runtime

@JvmInline
value class CapabilityId(val value: String)

data class CapabilitySet(
    val values: Set<CapabilityId> = emptySet()
) {
    operator fun contains(id: CapabilityId): Boolean = id in values

    fun containsAll(required: Set<CapabilityId>): Boolean =
        values.containsAll(required)
}

@JvmInline
value class TrainingProfileId(val value: String)
