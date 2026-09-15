# Project Decisions

This file records decisions that should survive future conversations.

## 2026-09-15

### Product
- Name/repository: EpsonRAsimulator.
- Primary platform: Android.
- Primary purpose: educational, intuitive robot simulator.
- Do not clone the existing Windows UI.
- 3D visualization is central, not decorative.
- Direct touch manipulation of the TCP is a core feature.
- Provide both direct Touch Move and ghost-preview Touch Target.
- Show joints and Cartesian coordinates together to teach their relationship.
- Teach Points are a core workflow.
- Programming and animation stay synchronized.
- Real accessories are represented as functional simulation modules.
- Initial accessory families: grippers, hands, vacuum and welding.
- Users should eventually be able to add custom tools.
- Tools change the active TCP and collision model.
- Workpieces should eventually be graspable.
- The app should support multiple Epson robot models through data-driven RobotDefinition objects.

### Confirmed Epson environment
- Windows software: EPSON RC+ 7.0.
- Installed version confirmed by user screenshot: 7.5.3.
- Configured robot: **Epson C4-A601S**.
- Robot type: six-axis articulated robot.
- Baseline reach: 600 mm.
- Baseline maximum payload: 4 kg.
- Epson publishes official C4-A601S STEP geometry; this is the preferred source for the simulator's 3D model.

### Architecture
- Kotlin + Android + Jetpack Compose.
- 3D direction: SceneView/Filament.
- Robot mathematics kept outside UI.
- External Epson connectivity isolated from simulation.
- Initial real-equipment behavior must never be implied by the simulated behavior.

### First usable milestone
3D C4-A601S + camera + joint control + direct TCP touch + IK + ghost target + teach points + functional gripper + simulated pick-and-place.

### Next technical work
- Acquire and convert official C4-A601S STEP geometry to optimized GLB/glTF.
- Establish exact link pivots and J1-J6 axes.
- Implement and validate forward kinematics.
- Connect joints to the 3D model.
- Implement touch TCP and inverse kinematics.
