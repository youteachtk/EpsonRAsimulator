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

## 2026-09-15 — Programming and project continuity

### GitHub as project source of truth
- GitHub is the durable technical memory for EpsonRAsimulator.
- Approved product decisions, architecture, specifications, implementation plans, roadmap state, and relevant technical discoveries must be written back to the repository.
- Future sessions must be able to recover project intent from the repository without depending on chat history.

### SPEL+ programming model
- Programming in the Android app must use **real Epson SPEL+**, not a disconnected educational pseudo-language.
- The app must support **bidirectional synchronization** between an intuitive visual editor and SPEL+ source:
  - visual actions -> internal program model -> SPEL+;
  - SPEL+ -> parser/internal program model -> visual representation.
- Visual and code views are two representations of the same program, not separate program formats.
- If imported SPEL+ contains syntax or instructions that the visual editor does not yet understand, the code must be preserved as an editable **Advanced SPEL+ / Direct Code** block.
- Unsupported SPEL+ must never be silently deleted, rewritten as something different, or rejected merely because the visual editor lacks a corresponding block.
- Round-trip compatibility and preservation of user-authored robot programs take priority over forcing every statement into a visual block.

### RC+ editor fidelity
- The programming experience should be **as close as practical to the official EPSON RC+ workflow**, while preserving the app's touch-first educational advantages.
- SPEL+ source remains a first-class view, with familiar concepts such as projects/files/functions, syntax highlighting, diagnostics, build/run controls, points and I/O context.
- The intuitive visual editor is an additional synchronized representation of the same SPEL+ program, not a replacement language.
- Prefer RC+-like terminology and workflow where verified, but do not clone the official UI pixel-for-pixel.
- When code is valid and supported, visual and source views stay synchronized. Unsupported/advanced SPEL+ remains preserved as editable direct-code blocks.
- Errors in SPEL+ must be surfaced without destroying the last valid visual representation or silently changing user code.

### RC+ familiarity + simulator advantage
- The app should intentionally feel familiar to a learner coming from **EPSON RC+**, because part of the product goal is to help users understand the official Epson environment rather than replace it with an unrelated workflow.
- Preserve verified RC+ concepts, terminology, project/program structure, points, I/O, robot management concepts, and execution mental models wherever practical.
- Do **not** copy the official interface pixel-for-pixel. Improve the experience where Android/touch/3D can make concepts clearer.
- The app's differentiators are:
  - substantially better 3D visualization of the robot and complete workcell;
  - direct touch manipulation and clearer robot controls;
  - synchronized Visual <-> SPEL+ programming;
  - educational explanations of unfamiliar RC+ concepts/screens;
  - digital-twin simulation of tools, actuators, sensors, parts, and processes;
  - the ability to expose advanced RC+-like functions progressively instead of overwhelming a new learner.
- The product should help the learner move between the Android app and the official RC+ software with minimal conceptual friction.

### Dual experience: RC+ Trainer and Visual Lab
- The app will have **two clearly separated user experiences**:
  1. **RC+ Trainer** — a learning-oriented simulator that stays as close as practical to the official EPSON RC+ concepts, terminology, structure, and workflows.
  2. **Visual Lab** — the app's own optimized interface for touch-first robot interaction, enhanced 3D simulation, workcell control, visual programming, and educational exploration.
- RC+ Trainer exists to help users learn the official software with minimal conceptual friction when they later use EPSON RC+ on Windows.
- Visual Lab is not constrained by the official RC+ screen layout and may use better tablet controls, richer 3D views, direct manipulation, and more intuitive visualizations.
- Both experiences operate on the same underlying robot/workcell/program/project state wherever practical, so they are different interfaces over the same simulation rather than separate products.
- Contextual **? help** is a core feature across both experiences. Help should explain what a screen, control, RC+ concept, SPEL+ instruction, point, I/O signal, or robot concept does without forcing the learner to leave the current task.
- Contextual help should distinguish clearly between:
  - verified EPSON RC+ behavior/concepts;
  - the app's own enhanced/educational behavior.
