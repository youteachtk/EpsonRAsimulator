package mx.youteachtk.epsonrasimulator.domain

enum class ToolCapability {
    OPEN_CLOSE,
    VACUUM,
    GRASP,
    WELD,
    CUSTOM_IO_SIM
}

data class ToolDefinition(
    val id: String,
    val displayName: String,
    val modelAsset: String? = null,
    val tcp: CartesianPose = CartesianPose(0.0, 0.0, 0.0),
    val capabilities: Set<ToolCapability> = emptySet()
)

sealed interface ToolCommand {
    data object OpenGripper : ToolCommand
    data object CloseGripper : ToolCommand
    data object VacuumOn : ToolCommand
    data object VacuumOff : ToolCommand
    data object WeldStart : ToolCommand
    data object WeldStop : ToolCommand
}
