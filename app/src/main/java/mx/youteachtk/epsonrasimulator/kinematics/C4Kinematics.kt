package mx.youteachtk.epsonrasimulator.kinematics

import mx.youteachtk.epsonrasimulator.domain.JointState
import mx.youteachtk.epsonrasimulator.domain.JointType

/**
 * CAD-derived kinematic chain for the Epson C4-A601S.
 *
 * Distances are millimetres. Joint origins/axes come from the official Epson
 * C4 CAD assembly already used by the 3D renderer.
 *
 * The flange-face offset is derived from the C4_J6 CAD geometry: the face is
 * approximately 65 mm along local -Z from the J6 axis intersection.
 *
 * IMPORTANT: RC+ coordinate-frame signs/orientation are still pending direct
 * validation against the user's RC+ 7.0 simulator. The CAD-space transform is
 * physical; [cadToRcCandidate] is a provisional right-handed mapping.
 */
object C4Kinematics {

    const val flangeOffsetMm: Double = 65.0

    val chain = KinematicChain(
        joints = listOf(
            KinematicJoint(
                id = "J1",
                type = JointType.REVOLUTE,
                parentToJoint = Matrix4.identity(),
                axis = Vector3.Y
            ),
            KinematicJoint(
                id = "J2",
                type = JointType.REVOLUTE,
                parentToJoint = Matrix4.translation(Vector3(0.0, 320.0, -100.0)),
                axis = Vector3.X
            ),
            KinematicJoint(
                id = "J3",
                type = JointType.REVOLUTE,
                parentToJoint = Matrix4.translation(Vector3(0.0, 250.0, 0.0)),
                axis = Vector3.X
            ),
            KinematicJoint(
                id = "J4",
                type = JointType.REVOLUTE,
                parentToJoint = Matrix4.identity(),
                axis = Vector3.Z
            ),
            KinematicJoint(
                id = "J5",
                type = JointType.REVOLUTE,
                parentToJoint = Matrix4.translation(Vector3(0.0, 0.0, -250.0)),
                axis = Vector3.X
            ),
            KinematicJoint(
                id = "J6",
                type = JointType.REVOLUTE,
                parentToJoint = Matrix4.identity(),
                axis = Vector3.Z
            )
        ),
        lastJointToTcp = Matrix4.translation(Vector3(0.0, 0.0, -flangeOffsetMm))
    )

    val calibrationPoseDegrees = listOf(
        20.0,
        -20.0,
        30.0,
        25.0,
        15.0,
        40.0
    )

    fun forward(jointsDegrees: List<Double>): ForwardKinematicsResult =
        SerialKinematics.forward(
            chain = chain,
            state = JointState(jointsDegrees)
        )

    fun tcpCadMm(jointsDegrees: List<Double>): Vector3 =
        forward(jointsDegrees).tcpPositionMm

    /**
     * Provisional conversion from the CAD frame (Y-up) to a conventional
     * right-handed Z-up robot frame:
     *
     * RC candidate X = CAD X
     * RC candidate Y = -CAD Z
     * RC candidate Z = CAD Y
     *
     * This mapping is deliberately labelled candidate until compared with
     * actual RC+ X/Y/Z values.
     */
    fun cadToRcCandidate(cad: Vector3): Vector3 =
        Vector3(
            x = cad.x,
            y = -cad.z,
            z = cad.y
        )

    fun tcpRcCandidateMm(jointsDegrees: List<Double>): Vector3 =
        cadToRcCandidate(tcpCadMm(jointsDegrees))
}
