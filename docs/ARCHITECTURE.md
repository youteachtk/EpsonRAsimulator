# Architecture

## Authoritative design spec

The approved architecture is fully defined in:

`docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`

This file remains a concise architectural overview. Where an older statement here conflicts with the formal design spec, the formal design spec takes precedence.

## Layering

### UI
Jetpack Compose screens and controls. Contains no robot mathematics.

### 3D Presentation
Scene graph, camera, visual robot links, tool assets, coordinate frames, ghost pose, paths and workcell visuals.

### Simulation Domain
Pure Kotlin concepts:
- joint state;
- pose;
- robot definition;
- tool definition;
- teach points;
- program actions;
- simulator state.

### Kinematics
Forward kinematics, IK, limit checking, singularity diagnostics and trajectory generation.

### Interaction
Converts touch gestures into target poses and determines whether the gesture manipulates camera, TCP, orientation handle, robot joint or workcell object.

### Persistence
Stores user projects, selected robot/tool, points, lessons and programs.

### External Integration
Reserved for future bridge protocols. It must not be imported by the core simulation module.

## Architectural components

The current approved architecture is organized around neutral contracts rather than a single RC+-specific application core.

Conceptual components:
- app-shell / navigation
- shared-runtime
- project-domain
- robot-domain / RobotProvider
- kinematics / motion
- task-runtime
- io-runtime
- workcell-runtime
- renderer / 3D presentation
- tool-runtime
- programming-core
- programming-language adapters (SPEL+ first)
- project-format adapters (RC+ first)
- simulator adapters (RC+ 7.0 first)
- RC+ Trainer workspace/window/command layer
- Visual Lab UI
- persistence / sidecar metadata
- learning / contextual help / localization
- bridge-protocol + vendor-specific bridge adapters (future)

Exact Gradle/module extraction is deferred to the implementation plan. The dependency rule is more important than the physical module count.

## State flow

Touch input
-> Interaction controller
-> Desired TCP pose
-> IK solver
-> Validation
-> Simulator state
-> 3D renderer + numeric UI

Program execution
-> native ProgramDocument / semantic model
-> TaskRuntime
-> command/motion/I-O/tool actions
-> Shared Runtime
-> 3D renderer + RC+ Trainer + Visual Lab

All user interfaces observe the same canonical runtime state.

## Robot definition strategy

Robot-specific values must not be hard-coded into UI code. A RobotDefinition owns:
- joint types and axes;
- min/max;
- link transforms;
- home position;
- model-node mapping;
- flange frame.

This lets later Epson models reuse the same UI and solvers.

## Tool strategy

Tools implement a capability contract instead of being special-cased in screens. Example capabilities:
- OPEN_CLOSE
- VACUUM
- GRASP
- WELD
- CUSTOM_IO_SIM

A change of tool changes the active TCP and collision model.

## Testing strategy

- pure unit tests for transforms and FK;
- known-pose tests per robot;
- IK round-trip tests;
- joint-limit tests;
- gesture-to-target tests;
- program-engine tests;
- accessory-state tests;
- screenshot/UI tests later;
- physical robot behavior is never assumed from simulator tests.

## Multi-robot / multi-simulator extension architecture

The shared runtime must not depend on the C4-A601S or RC+ as hard-coded global assumptions.

### RobotProvider / RobotDefinition
A robot package supplies:
- robot identity/version metadata;
- kinematic chain;
- joint axes/types/limits;
- frames and calibration;
- motion capability metadata;
- render/collision asset references and provenance;
- supported tool interfaces;
- optional robot-specific diagnostics.

The generic kinematics/simulation layers consume these contracts.

### SimulatorAdapter
A simulator/trainer package owns vendor/environment-specific behavior:
- menu/window/tool registry;
- workflow semantics and shortcuts;
- profile/capability rules;
- project-resource model;
- build/run/debug semantics;
- contextual-help catalog;
- optional integration/bridge contract.

The RC+ Trainer becomes the first SimulatorAdapter rather than the definition of the entire application core.

### ProgrammingLanguageAdapter
Programming semantics must be pluggable:
- parser/token model;
- diagnostics;
- source preservation;
- formatter/generator where safe;
- executable simulation subset;
- debugger/task integration.

SPEL+ is the first adapter. Future simulator environments may use different languages without forcing those languages into SPEL+ concepts.

### ProjectFormatAdapter
Native project import/export remains simulator-specific.
An adapter may understand some resources semantically and preserve others as opaque data.
No generic layer may destructively rewrite unknown source-project data.

### Capability and fidelity metadata
Every catalog package should declare:
- supported features;
- required controller/options;
- documentation/reference provenance;
- implementation status;
- fidelity status;
- asset provenance/licensing status.

This allows the app to distinguish Verified, Simulated, Partial and Unsupported behavior rather than presenting all catalog entries as equally complete.

### Dependency rule
Core simulation/kinematics/workcell modules must not import RC+-specific UI or Epson project-format code.
Vendor/simulator packages may depend on the neutral contracts, never the reverse.