# Epson RA Simulator

Android-first educational simulator for learning Epson industrial robot motion, teaching points, end effectors and programming through an intuitive 3D interface.

> Project status: Phase 0 — foundation and architecture.

## Vision

The app is not intended to clone Epson RC+. Its purpose is to make robot behavior easy to understand visually before a learner works with a real robot.

Core ideas:
- interactive 3D robot;
- direct touch manipulation of the TCP;
- joint and Cartesian jog modes;
- inverse kinematics (IK);
- ghost preview before simulated moves;
- teach points / point table;
- programmable simulated sequences;
- modular real-world tools and end effectors;
- learning exercises;
- later, optional connection to Epson RC+ through a separate desktop bridge.

## Planned tool modules

- two-finger gripper;
- vacuum gripper;
- robotic hand;
- welding torch;
- custom tool definition;
- future sensor/process modules.

Tools are functional simulation modules, not decorative models. Each tool can define its own TCP, collision volume, state, commands and process behavior.

## Safety boundary

The first product is simulation-only. Any future control of physical equipment must be implemented as a separate, explicitly enabled subsystem with independent safety requirements. See [docs/SAFETY-BOUNDARY.md](docs/SAFETY-BOUNDARY.md).

## Documentation

- [Master plan](docs/MASTER-PLAN.md)
- [Architecture](docs/ARCHITECTURE.md)
- [Accessory system](docs/ACCESSORIES.md)
- [Interaction model](docs/INTERACTION.md)
- [Development roadmap](docs/ROADMAP.md)
- [Project decisions](docs/DECISIONS.md)
- [Safety boundary](docs/SAFETY-BOUNDARY.md)

## Initial Android baseline

- Android / Kotlin
- Jetpack Compose
- Material 3
- SceneView + Filament for 3D
- modular simulation/domain layer independent of UI

The exact Epson robot model will be added as robot-definition data once identified.
