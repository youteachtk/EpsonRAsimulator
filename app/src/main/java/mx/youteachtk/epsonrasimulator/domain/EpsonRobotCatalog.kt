package mx.youteachtk.epsonrasimulator.domain

object EpsonRobotCatalog {
    /**
     * Exact robot confirmed from the user's EPSON RC+ 7.0 Robot Manager:
     * C4-A601S.
     *
     * Motion ranges, encoder pulse ranges and maximum joint speeds are from
     * Epson C4 documentation for the standard (non-UL) C4-A601S.
     */
    val C4_A601S = RobotDefinition(
        id = "epson-c4-a601s",
        displayName = "Epson C4-A601S",
        joints = listOf(
            JointDefinition(
                id = "J1",
                displayName = "Base rotation",
                type = JointType.REVOLUTE,
                minValue = -170.0,
                maxValue = 170.0,
                maxSpeedDegPerSec = 450.0,
                minPulse = -4_951_609,
                maxPulse = 4_951_609
            ),
            JointDefinition(
                id = "J2",
                displayName = "Lower arm",
                type = JointType.REVOLUTE,
                minValue = -160.0,
                maxValue = 65.0,
                maxSpeedDegPerSec = 450.0,
                minPulse = -4_660_338,
                maxPulse = 1_893_263
            ),
            JointDefinition(
                id = "J3",
                displayName = "Upper arm",
                type = JointType.REVOLUTE,
                minValue = -51.0,
                maxValue = 225.0,
                maxSpeedDegPerSec = 514.0,
                minPulse = -1_299_798,
                maxPulse = 5_734_400
            ),
            JointDefinition(
                id = "J4",
                displayName = "Wrist roll",
                type = JointType.REVOLUTE,
                minValue = -200.0,
                maxValue = 200.0,
                maxSpeedDegPerSec = 555.0,
                minPulse = -4_723_316,
                maxPulse = 4_723_316
            ),
            JointDefinition(
                id = "J5",
                displayName = "Wrist bend",
                type = JointType.REVOLUTE,
                minValue = -135.0,
                maxValue = 135.0,
                maxSpeedDegPerSec = 555.0,
                minPulse = -3_188_238,
                maxPulse = 3_188_238
            ),
            JointDefinition(
                id = "J6",
                displayName = "Wrist twist",
                type = JointType.REVOLUTE,
                minValue = -360.0,
                maxValue = 360.0,
                maxSpeedDegPerSec = 720.0,
                minPulse = -6_553_600,
                maxPulse = 6_553_600
            )
        ),
        reachMm = 600.0,
        wristFlangeReachMm = 665.0,
        ratedPayloadKg = 1.0,
        maxPayloadKg = 4.0,
        modelAsset = "models/robots/c4-a601s/robot.glb",
        zeroJointValues = listOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    )
}
