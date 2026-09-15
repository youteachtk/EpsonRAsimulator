package mx.youteachtk.epsonrasimulator.domain

data class JointDefinition(
    val id: String,
    val displayName: String,
    val type: JointType,
    val minValue: Double,
    val maxValue: Double
)

enum class JointType {
    REVOLUTE,
    PRISMATIC
}

data class RobotDefinition(
    val id: String,
    val displayName: String,
    val joints: List<JointDefinition>,
    val reachMm: Double? = null,
    val maxPayloadKg: Double? = null,
    val modelAsset: String? = null
)

data class JointState(
    val values: List<Double>
)

data class CartesianPose(
    val x: Double,
    val y: Double,
    val z: Double,
    val rx: Double = 0.0,
    val ry: Double = 0.0,
    val rz: Double = 0.0
)

data class TeachPoint(
    val name: String,
    val pose: CartesianPose,
    val preferredJointState: JointState? = null
)
