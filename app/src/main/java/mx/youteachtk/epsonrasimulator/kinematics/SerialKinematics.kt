package mx.youteachtk.epsonrasimulator.kinematics

import mx.youteachtk.epsonrasimulator.domain.JointState
import mx.youteachtk.epsonrasimulator.domain.JointType

/**
 * One joint in a serial kinematic chain.
 *
 * parentToJoint is the fixed transform from the previous moving frame to
 * this joint's zero-position frame. axis is expressed in the joint frame.
 */
data class KinematicJoint(
    val id: String,
    val type: JointType,
    val parentToJoint: Matrix4,
    val axis: Vector3
)

data class KinematicChain(
    val joints: List<KinematicJoint>,
    val lastJointToTcp: Matrix4 = Matrix4.identity()
)

data class ForwardKinematicsResult(
    val baseToJointFrames: List<Matrix4>,
    val baseToTcp: Matrix4
) {
    val tcpPositionMm: Vector3
        get() = baseToTcp.translation
}

object SerialKinematics {

    fun forward(
        chain: KinematicChain,
        state: JointState
    ): ForwardKinematicsResult {
        require(chain.joints.size == state.values.size) {
            "Kinematic chain and joint state must contain the same number of joints"
        }

        var transform = Matrix4.identity()
        val frames = ArrayList<Matrix4>(chain.joints.size)

        chain.joints.forEachIndexed { index, joint ->
            transform = transform * joint.parentToJoint

            val value = state[index]
            transform = when (joint.type) {
                JointType.REVOLUTE ->
                    transform * Matrix4.rotation(joint.axis, value)

                JointType.PRISMATIC ->
                    transform * Matrix4.translation(
                        joint.axis.normalized().let { axis ->
                            Vector3(axis.x * value, axis.y * value, axis.z * value)
                        }
                    )
            }

            frames += transform
        }

        return ForwardKinematicsResult(
            baseToJointFrames = frames,
            baseToTcp = transform * chain.lastJointToTcp
        )
    }
}
