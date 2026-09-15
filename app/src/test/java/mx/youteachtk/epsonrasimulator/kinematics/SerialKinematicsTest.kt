package mx.youteachtk.epsonrasimulator.kinematics

import mx.youteachtk.epsonrasimulator.domain.JointState
import mx.youteachtk.epsonrasimulator.domain.JointType
import org.junit.Assert.assertEquals
import org.junit.Test

class SerialKinematicsTest {

    @Test
    fun oneJointRotationMovesTcpAroundZ() {
        val chain = KinematicChain(
            joints = listOf(
                KinematicJoint(
                    id = "J1",
                    type = JointType.REVOLUTE,
                    parentToJoint = Matrix4.identity(),
                    axis = Vector3.Z
                )
            ),
            lastJointToTcp = Matrix4.translation(Vector3(100.0, 0.0, 0.0))
        )

        val result = SerialKinematics.forward(
            chain = chain,
            state = JointState(listOf(90.0))
        )

        assertEquals(0.0, result.tcpPositionMm.x, 1e-9)
        assertEquals(100.0, result.tcpPositionMm.y, 1e-9)
        assertEquals(0.0, result.tcpPositionMm.z, 1e-9)
    }

    @Test
    fun twoJointPlanarArmProducesExpectedTcpPosition() {
        val chain = KinematicChain(
            joints = listOf(
                KinematicJoint(
                    id = "J1",
                    type = JointType.REVOLUTE,
                    parentToJoint = Matrix4.identity(),
                    axis = Vector3.Z
                ),
                KinematicJoint(
                    id = "J2",
                    type = JointType.REVOLUTE,
                    parentToJoint = Matrix4.translation(Vector3(100.0, 0.0, 0.0)),
                    axis = Vector3.Z
                )
            ),
            lastJointToTcp = Matrix4.translation(Vector3(100.0, 0.0, 0.0))
        )

        val result = SerialKinematics.forward(
            chain = chain,
            state = JointState(listOf(0.0, 90.0))
        )

        assertEquals(100.0, result.tcpPositionMm.x, 1e-9)
        assertEquals(100.0, result.tcpPositionMm.y, 1e-9)
        assertEquals(0.0, result.tcpPositionMm.z, 1e-9)
    }
}
