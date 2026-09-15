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

### Architecture
- Kotlin + Android + Jetpack Compose.
- 3D direction: SceneView/Filament.
- Robot mathematics kept outside UI.
- External Epson connectivity isolated from simulation.
- Initial real-equipment behavior must never be implied by the simulated behavior.

### First usable milestone
3D robot + camera + joint control + direct TCP touch + IK + ghost target + teach points + functional gripper + simulated pick-and-place.

### Pending
Exact Epson robot model and Windows Epson software/version still need to be identified.
