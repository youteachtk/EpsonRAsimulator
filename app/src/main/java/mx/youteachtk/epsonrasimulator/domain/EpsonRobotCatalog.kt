package mx.youteachtk.epsonrasimulator.domain

object EpsonRobotCatalog {
    /**
     * Exact robot confirmed from the user's EPSON RC+ 7.0 Robot Manager:
     * C4-A601S.
     *
     * Joint limits, reach and payload are based on Epson C4 documentation.
     * Values are expressed in degrees for all six revolute joints.
     */
    val C4_A601S = RobotDefinition(
        id = "epson-c4-a601s",
        displayName = "Epson C4-A601S",
        joints = listOf(
            JointDefinition("J1", "Joint 1", JointType.REVOLUTE, -170.0, 170.0),
            JointDefinition("J2", "Joint 2", JointType.REVOLUTE, -160.0, 65.0),
            JointDefinition("J3", "Joint 3", JointType.REVOLUTE, -51.0, 225.0),
            JointDefinition("J4", "Joint 4", JointType.REVOLUTE, -200.0, 200.0),
            JointDefinition("J5", "Joint 5", JointType.REVOLUTE, -135.0, 135.0),
            JointDefinition("J6", "Joint 6", JointType.REVOLUTE, -360.0, 360.0)
        ),
        reachMm = 600.0,
        maxPayloadKg = 4.0,
        modelAsset = "models/robots/c4-a601s/robot.glb"
    )
}
