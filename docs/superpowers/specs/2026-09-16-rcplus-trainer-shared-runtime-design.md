# RC+ Trainer + Visual Lab Shared Runtime Design

**Date:** 2026-09-16  
**Status:** Final architecture design approved by user; implementation planning authorized  
**Reference baseline:** EPSON RC+ 7.0 v7.5.3 + C4-A601S school environment  
**Primary platform:** Android, Kotlin, Jetpack Compose, SceneView/Filament  
**Design method:** Superpowers brainstorming/design approval

## 1. Purpose

This document defines the approved product and software architecture for the next major evolution of EpsonRAsimulator.

The product is no longer treated as a single C4-A601S demo. It is designed as an extensible industrial-robot training platform with:

1. **RC+ Trainer** — a high-fidelity educational reproduction of verified EPSON RC+ workflows and mental models.
2. **Visual Lab** — an independent touch-first 3D interface for understanding robot motion, workcells, I/O, tools, programs and digital-twin behavior.
3. **One neutral shared runtime** — the canonical source of truth for project, program, robot, task, I/O and workcell state.
4. **Extensible robot/simulator adapters** — the C4-A601S and RC+ 7.0 are the first reference packages, not permanent limits.

The architecture must let a learner practice workflows that transfer to the real software while also providing a substantially clearer visual learning environment.

## 2. Product goals

### 2.1 Learning-transfer goal

A learner who practices in RC+ Trainer should recognize the same concepts, terminology, navigation structure and workflows when later using real EPSON RC+.

Verified RC+ behavior is preserved by default when it materially improves learning transfer.

### 2.2 Visual-understanding goal

Visual Lab should make robot behavior easier to understand through:

- direct 3D interaction;
- TCP manipulation;
- joint and Cartesian feedback;
- workcell visualization;
- synchronized programs and animation;
- live I/O and task state;
- functional sensors, actuators, tools and parts;
- guided bilingual explanations.

### 2.3 Project-preservation goal

Imported native projects and source files must be preserved safely.

The application must not destroy or silently rewrite project resources merely because it does not understand them.

### 2.4 Extensibility goal

The architecture must support future additions of:

- other Epson robots;
- other Epson controller / RC+ versions;
- additional simulator ecosystems;
- other manufacturers where sufficient reliable documentation and lawful asset paths exist.

No catalog item may claim high fidelity without verified behavior and adequate technical data.

## 3. Non-goals

This design does not authorize:

- pixel-for-pixel copying of EPSON RC+;
- copying Epson icons, logos, screenshots or proprietary artwork;
- reverse engineering/decompilation of proprietary binaries;
- silently redistributing proprietary CAD assets;
- claiming unsupported code is simulated correctly;
- treating RC+ Digital Twin as real-robot control;
- enabling physical robot control in the initial implementation plan;
- inventing undocumented behavior to make a screen look complete.

## 4. Product experiences

### 4.1 RC+ Trainer

RC+ Trainer is an embedded desktop-style training environment.

Its baseline target is EPSON RC+ 7.0 v7.5.3 as verified in the school environment.

It reproduces the documented mental model:

- main parent workspace;
- menu bar;
- main toolbar;
- Project Explorer;
- Status pane;
- status bar;
- MDI-style internal child windows;
- Robot Manager;
- source/program windows;
- point files;
- Command Window;
- I/O Monitor;
- Task Manager;
- Controller and Setup dialogs/tools;
- Run / Operator workflows;
- other verified tools and optional modules.

Functional and structural fidelity is required. Visual assets and styling remain independently designed.

### 4.2 Visual Lab

Visual Lab is a separate Android-native experience.

It is not constrained by RC+'s desktop layout.

It may use:

- richer 3D;
- direct touch manipulation;
- simplified controls;
- visual programming;
- educational overlays;
- workcell controls;
- guided explanations.

Visual Lab and RC+ Trainer observe and control the same underlying project/runtime state.

### 4.3 Startup and resume

When no active work exists, the user sees two primary entries:

- RC+ Trainer
- Visual Lab

When active work exists, the app resumes the last active experience and working context.

The user can switch experiences without duplicating project state.

## 5. Approved top-level architecture

The approved architecture is **Option B: neutral shared runtime + two independent interfaces**.

```text
┌──────────────────────┐      ┌──────────────────────┐
│     RC+ Trainer      │      │      Visual Lab      │
│ high-fidelity RC+ UI │      │  touch-first 3D UI   │
└──────────┬───────────┘      └──────────┬───────────┘
           │                             │
           └──────────────┬──────────────┘
                          ▼
              ┌──────────────────────┐
              │    SHARED RUNTIME    │
              │ Project State        │
              │ Program Model        │
              │ Points               │
              │ Robot State          │
              │ Motion/Kinematics    │
              │ Tasks                │
              │ I/O                  │
              │ Workcell             │
              │ Tools/TCP            │
              │ Diagnostics          │
              │ Profiles/Capabilities│
              └──────────┬───────────┘
                         │
             ┌───────────┴────────────┐
             ▼                        ▼
     Persistence / formats      External bridges
```

Neither UI owns authoritative project or robot state.

Both interfaces issue commands/actions to the shared runtime and observe resulting state.

## 6. Core architectural rule: single source of truth

There must not be:

- one robot state for RC+ Trainer and another for Visual Lab;
- one I/O table for I/O Monitor and another for the workcell;
- one task list for Task Manager and another for program execution;
- one point list for Visual Lab and another for .pts files.

Canonical runtime services own these domains.

Examples:

- changing J2 in Robot Manager changes the same robot state rendered in Visual Lab;
- teaching P1 in Visual Lab updates the same point resource seen in RC+ Trainer;
- setting Output 5 in I/O Monitor changes the same output consumed by workcell actuators;
- pausing a task in Task Manager pauses the real simulated task executing the program.

## 7. Neutral runtime boundaries

The shared runtime should expose neutral contracts rather than RC+-specific UI assumptions.

Conceptual services include:

- ProjectRuntime
- ProgramRuntime
- RobotRuntime
- MotionRuntime
- TaskRuntime
- IoRuntime
- WorkcellRuntime
- ToolRuntime
- DiagnosticRuntime
- SimulationClock
- CapabilityService
- CommandGateway

Names may change during implementation planning, but responsibilities should remain separated.

## 8. Robot package architecture

The C4-A601S is the first RobotProvider / RobotDefinition package.

A robot package should provide, as available:

- robot identity/version metadata;
- joint count/types;
- kinematic chain;
- joint axes;
- joint limits;
- speed/motion capability metadata;
- base/flange frames;
- calibration/home poses;
- renderer node mapping;
- collision geometry;
- tool-mount metadata;
- supported robot capabilities;
- diagnostic metadata;
- asset provenance.

Generic simulation and kinematics modules consume the robot contract.

They must not hard-code C4-A601S constants in UI code.

## 9. Simulator adapter architecture

RC+ 7.0 v7.5.3 is the first SimulatorAdapter.

A simulator adapter supplies environment-specific behavior such as:

- menu registry;
- toolbar registry;
- window/tool registry;
- shortcut registry;
- project resource rules;
- build/run/debug workflows;
- capability/profile rules;
- contextual help IDs;
- programming-language adapter selection;
- project-format adapter selection;
- optional bridge contract.

Future examples may include:

- Epson RC+ 8.x;
- other Epson environments;
- other manufacturers where fidelity can be documented responsibly.

The shared runtime must never import vendor-specific UI code.

## 10. Programming document architecture

### 10.1 Native source preservation

Native source is authoritative for preservation.

For SPEL+, the programming layer must retain:

- original source text;
- comments;
- whitespace/trivia;
- token/concrete-syntax information;
- understood semantic nodes;
- unsupported/opaque regions.

A lossy AST-only model is insufficient.

### 10.2 Semantic program model

Understood source is represented semantically so it can drive:

- diagnostics;
- Visual Lab;
- simulation;
- task execution;
- source navigation;
- build/run behavior.

### 10.3 Four support states

A source region/document can be:

1. **Valid and supported**
   - source, semantic model, visual representation and local simulation synchronize.

2. **Valid and partially supported**
   - all source is preserved;
   - known regions can be visualized;
   - unknown regions remain editable Advanced SPEL+ / Direct Code.

3. **Valid for native RC+ but not locally simulatable**
   - source remains editable/exportable;
   - Local Simulation clearly marks the unsupported execution region.

4. **Syntax-invalid**
   - exact source text is preserved;
   - diagnostics are shown;
   - the last valid semantic/visual representation is retained rather than destroyed.

### 10.4 Visual Lab programming

Visual Lab is not a separate pseudo-language.

Visual program actions are an alternate representation of the same semantic program.

Where a language adapter supports it, visual editing modifies/generates native source.

Unsupported native code remains visible as direct/advanced source rather than being discarded.

## 11. ProgrammingLanguageAdapter

SPEL+ is the first ProgrammingLanguageAdapter.

A language adapter owns, as appropriate:

- lexer/token model;
- parser/concrete syntax representation;
- semantic model mapping;
- diagnostics;
- source-preserving edits;
- formatter/generator where safe;
- local executable subset;
- debugger/task integration;
- visual representation mappings.

Future languages must not be forced into SPEL+ semantics.

Cross-language conversion is never assumed to be lossless.

## 12. Project format and resource model

### 12.1 Resource categories

Imported native project resources are classified conceptually as:

- NativeKnownEditable
- NativeKnownPreserved
- NativeOpaque
- AppSidecarMetadata

### 12.2 RC+ project baseline

Known resources currently include concepts such as:

- .sprj project resources;
- .prg program files;
- .inc include files;
- .pts point files;
- .mac macro resources;
- I/O labels;
- user errors;
- controller/project configuration resources.

Exact handling depends on independently verified format behavior.

### 12.3 Opaque preservation

Unknown or proprietary resources must be retained unchanged whenever technically possible.

The application must not guess or destructively regenerate an opaque .sprj structure.

### 12.4 App metadata separation

App-only information stays outside native project resources, for example:

- RC+ Trainer internal window geometry;
- Visual Lab layout data;
- 3D camera state;
- tutorial progress;
- notes;
- app-specific workcell enhancements;
- touch/accessibility preferences.

## 13. Round-trip behavior

The import/edit/export goal is:

```text
Native project
    ↓
ProjectFormatAdapter
    ↓
Known semantic resources + preserved opaque resources
    ↓
User edits supported content
    ↓
Export
    ↓
Native project with untouched unknown content preserved
```

Unchanged resources should not be regenerated unnecessarily.

Modified known resources are updated using format-aware logic.

Unknown resources are preserved byte-for-byte when feasible.

## 14. Workcell runtime

The WorkcellRuntime is functional, not decorative.

It supports reusable component-based entities.

Candidate components include:

- Transform
- Renderable
- CollisionShape
- RigidBody
- Graspable
- Fixture
- Sensor
- Actuator
- SignalBinding
- Conveyor
- AuxiliaryAxis
- SpawnSource
- ProcessTarget
- Pallet
- ToolMount
- CustomProcessComponent

The specific component API will be defined in implementation planning.

## 15. Functional I/O

I/O is shared state.

A sensor can update an input.

A task/program can react to that input.

A program can set an output.

An output can drive a simulated actuator.

The same state appears in I/O Monitor.

Example:

```text
Part reaches sensor
  ↓
Input 3 = ON
  ↓
SPEL+ Wait condition releases
  ↓
Task changes state
  ↓
Output 5 = ON
  ↓
Cylinder extends
  ↓
Workcell 3D updates
```

I/O must support labels and capability/profile-dependent ranges.

## 16. Tool and grasp runtime

Tools are functional modules.

A ToolDefinition may provide:

- mount transform;
- TCP;
- collision shapes;
- process state;
- I/O bindings;
- capabilities;
- renderer assets;
- grasp/release rules;
- process visualization.

Initial tool families:

- two-finger gripper;
- vacuum;
- welding tool;
- articulated hand;
- future custom tools.

Changing tools changes active TCP and collision behavior.

## 17. Task runtime

TaskRuntime is the canonical execution model for simulated programs.

Run Window, Task Manager, source debugging and Command Window observe the same task state.

TaskRuntime must support concepts needed by the verified RC+ workflow, including:

- task identity;
- function/program/line context;
- running/waiting/halted/paused/finished/aborted states;
- stepping;
- resume;
- stop;
- breakpoints;
- debug source location;
- simulated metrics where appropriate.

Simulated values such as CPU load must be clearly represented as simulated unless sourced from a real controller.

## 18. Shared simulation clock

Robot motion, task waits, timers, conveyors, actuators and sensor updates use one simulation-time authority.

The clock should support:

- run;
- pause;
- deterministic step where practical;
- speed scaling where safe for simulation;
- reproducible tests.

The exact scheduler design is deferred to implementation planning.

## 19. RC+ Trainer workspace architecture

### 19.1 Workspace

RC+ Trainer uses an internal desktop/MDI metaphor because the real environment is multi-window.

Structural elements:

- menu bar;
- main toolbar;
- Project Explorer;
- child-window area;
- Status pane;
- status bar;
- minimized-window bar/taskbar.

This is not a general-purpose operating-system shell.

### 19.2 RcWindowManager

RcWindowManager owns internal child-window state:

- open;
- close;
- focus;
- z-order;
- move;
- resize;
- minimize;
- maximize;
- restore;
- cascade;
- tile;
- geometry persistence.

### 19.3 Form-factor adaptation

Landscape tablet is the primary fidelity format.

Phone/portrait uses the same workspace/window state, but may present the active child maximized with a compact window switcher.

Changing screen size/orientation must not conceptually close tools or create a separate simplified RC+ product.

## 20. Command architecture

RcCommandRegistry is the single command model behind:

- menu items;
- toolbar buttons;
- shortcuts;
- context menus;
- command surfaces.

Example:

```text
Tools > Robot Manager
Toolbar button
F6
      ↓
OPEN_ROBOT_MANAGER
      ↓
RcWindowManager
```

All entry points share:

- execution;
- enable/disable state;
- capability checks;
- contextual help metadata.

## 21. Tool/window registry

RcToolRegistry defines tools/windows such as:

- Robot Manager;
- Command Window;
- I/O Monitor;
- Task Manager;
- Macros;
- I/O Label Editor;
- User Error Editor;
- Controller;
- Simulator;
- optional Vision/Force/GUI/Conveyor/Part Feeding modules.

Descriptors include:

- command ID;
- menu placement;
- toolbar placement;
- shortcut;
- child-window/dialog type;
- capability requirements;
- profile availability;
- help ID;
- implementation/fidelity status.

## 22. RC+ fidelity rule

Verified RC+ behavior that improves learning transfer is preserved by default.

This applies to:

- menu names/structure;
- tool/window names;
- Project Explorer behavior;
- file/project workflows;
- Build/Run;
- debugging;
- editor interactions;
- point management;
- Robot Manager;
- I/O;
- Task Manager;
- Command Window;
- Controller/Setup;
- shortcuts;
- status/error behavior;
- window management.

Do not repeatedly re-approve minor fidelity decisions once official behavior is verified.

Escalate only for:

- ambiguous/version-dependent behavior;
- meaningful Android adaptation;
- legal/IP concern;
- safety concern;
- material design fork.

## 23. Touch and input adaptation

Desktop interactions remain available when mouse/keyboard is connected.

Approved touch adaptations include:

- single tap = select;
- double tap = open/jump where desktop uses double-click;
- long-press = context menu where desktop uses right-click;
- drag title bar = move internal child window;
- double-tap title bar = maximize/restore;
- larger invisible resize hit targets;
- one-tap restore from minimized-window bar.

Keyboard shortcuts are reproduced where verified and safe.

## 24. Robot Manager architecture

Robot Manager is a substantial subsystem.

For the verified C4-class environment, documented page families include:

- Control Panel;
- Jog & Teach;
- Points;
- Hands;
- Arch;
- Locals;
- Tools;
- Pallets;
- ECP;
- Boxes;
- Planes;
- Weight.

Other pages may be robot/controller/option dependent.

Robot Manager uses a page registry with capability predicates rather than a universal hard-coded page list.

Full field-level fidelity is verified per page before a page is marked High Fidelity.

## 25. Profiles and capability model

### 25.1 School Setup

Represents the verified school environment as closely as possible:

- C4-A601S;
- EPSON RC+ 7.0 v7.5.3;
- verified controller/options;
- verified feature availability.

### 25.2 Full Learning

Exposes documented learning modules even when not installed at school.

Each capability can carry metadata such as:

- standard;
- optional software;
- optional hardware;
- robot/controller dependent;
- School availability: yes/no/unknown;
- implementation status;
- fidelity status.

Full Learning availability does not imply real hardware/license availability.

## 26. Optional/full-learning domains

The architecture must be able to accommodate documented domains including:

- Vision Guide;
- Force Guide / Force Control / Force Monitor;
- GUI Builder;
- Conveyor Tracking;
- Part Feeding;
- RC+ API;
- Security;
- PG Motion / auxiliary axes;
- ECP;
- Fieldbus and expansion I/O;
- teach-pendant/external-device learning;
- controller communications.

These should be implemented only when sufficiently verified.

## 27. Localization

The entire application is bilingual English/Spanish.

Localization includes:

- RC+ Trainer;
- Visual Lab;
- navigation;
- dialogs;
- simulator/workcell controls;
- diagnostics;
- lessons;
- contextual help;
- settings.

Exact code/identifiers are never translated:

- SPEL+ keywords;
- source code;
- point names;
- I/O identifiers;
- user-defined names;
- exact technical tokens.

English is the reference terminology for RC+-specific concepts.

Where a safe translation does not exist, retain the English term and provide a Spanish explanation.

## 28. Contextual help

Help content is original, bilingual and data-driven.

Controls/windows reference help IDs, for example:

```text
rc7.robotManager.jogTeach
```

Help can explicitly label:

- Verified RC+ behavior
- Training simulation behavior
- Requires hardware/option
- Partial support
- Not yet simulated

Manual prose/screenshots are not copied into the app.

## 29. Visual originality and IP boundary

Preserve functional names where needed for authentic learning.

Do not copy:

- proprietary icons;
- logos;
- screenshots;
- artwork;
- exact window chrome;
- proprietary help prose.

Use independently designed:

- icons;
- styling;
- typography;
- spacing;
- illustrations;
- visual assets.

EPSON / EPSON RC+ are descriptive compatibility/training references, not product branding.

## 30. 3D asset boundary

The current CAD-derived C4 model is a development/reference asset with unresolved commercial redistribution status.

Commercial release should prefer an independently authored C4-A601S-compatible visual model based on lawful factual references, measurements and original modeling.

Kinematics, axes, pivots, TCP and collision logic remain independent of the render mesh so assets can be swapped.

Every future robot/tool/workcell asset records provenance.

## 31. Persistence

Local/offline operation is the default.

The app should locally persist:

- project selection;
- native project resources;
- app sidecar metadata;
- workspace/window state;
- active mode;
- workcell state where appropriate;
- learning progress;
- user preferences.

Cloud sync, school management or collaboration are optional future services, not runtime prerequisites.

## 32. Windows bridge architecture

Android does not embed Windows-specific Epson integration.

Boundary:

```text
Android Shared Runtime
        ↕
Bridge Protocol
        ↕
Windows Epson Bridge
        ↕
EPSON RC+
```

The Windows bridge is responsible for using whatever Epson interfaces are officially supported/documented.

Bridge implementation details require separate research before development.

## 33. Connection modes and authority

### 33.1 Local Simulation

The Shared Runtime is authoritative.

All robot/task/I/O/workcell behavior is simulated locally.

### 33.2 RC+ Digital Twin

The real RC+ Windows simulation/controller session is authoritative for live controller state.

Examples:

- robot pose;
- task status;
- motors;
- live I/O;
- controller errors;
- execution status.

Android-initiated actions go through CommandGateway and must await bridge/native confirmation when the external environment is authoritative.

### 33.3 Future Real Hardware

Real robot control is a separate future mode.

It requires its own explicit:

- permission model;
- safety design;
- interlocks;
- hardware/controller validation;
- E-stop/safeguard boundaries;
- user confirmation model.

Digital-twin connectivity must never silently enable physical robot control.

## 34. Project synchronization vs live synchronization

These are separate channels.

### Project synchronization

Examples:

- .prg;
- .inc;
- .pts;
- project configuration.

### Live synchronization

Examples:

- robot position;
- task state;
- I/O;
- motors;
- errors;
- execution.

A project may synchronize without a live execution session.

A live session may be observed without modifying project files.

## 35. Conflict handling

Simultaneous edits from Android and Windows must never silently overwrite one another.

Conflicts should be explicit.

Example:

```text
Main.prg changed in Android
Main.prg changed in RC+

Conflict:
- Compare
- Keep Android
- Keep RC+
- future merge
```

The exact UX is deferred, but silent last-writer-wins is not acceptable for native source.

## 36. External bridge adapters

Future simulator ecosystems may provide their own bridge adapters.

Conceptual pattern:

- EpsonRcPlusBridgeAdapter
- future vendor adapters

The shared runtime is not coupled to one vendor's external API.

## 37. Multi-robot / multi-simulator catalog

The C4-A601S + RC+ 7.0 package is the first catalog entry.

Future packages require sufficient trustworthy information:

- official/public technical documentation;
- kinematic/controller data;
- programming/workflow references;
- lawful 3D assets or enough factual information to create independent models.

Each package declares:

- supported features;
- required controller/options;
- reference provenance;
- implementation status;
- fidelity status;
- asset provenance/license status.

Unsupported or uncertain behavior is identified rather than guessed.

## 38. Competitive-positioning implication

Market research shows that individual features already exist across products such as:

- Epson RC+/RC+ Express;
- RoboDK;
- Visual Components;
- ABB RobotStudio;
- FANUC ROBOGUIDE;
- KUKA.Sim;
- Universal Robots URSim/Academy.

The target differentiation is the combination of:

1. high-fidelity learning of real vendor software/workflows;
2. a friendlier Visual Lab over the same canonical runtime;
3. touch-first/mobile availability;
4. preservation of real vendor-language source;
5. bilingual contextual teaching;
6. modular expansion to additional simulator ecosystems while preserving each native mental model.

The product should not be positioned merely as "a 3D robot simulator."

## 39. Error and diagnostic model

Diagnostics from multiple subsystems should converge into a shared event model.

Potential sources:

- parser;
- build;
- runtime;
- task;
- robot motion;
- kinematics;
- joint limits;
- singularities;
- collision;
- I/O;
- workcell;
- bridge;
- project import/export.

Each diagnostic should carry enough context for both:

- RC+ Trainer presentation;
- Visual Lab educational explanation.

Native RC+ error numbers/messages should only be shown as native when actually verified or received from RC+.

Locally generated simulator diagnostics must not masquerade as controller-native errors.

## 40. Safety boundaries

Simulation status must be visually distinguishable from live/connected modes.

Never imply:

- simulated Motor On means physical motor power;
- simulated safeguard state controls a real safeguard;
- simulated E-stop is a certified safety system;
- local collision checking guarantees physical safety.

Real-hardware integration remains outside this implementation design.

## 41. Testing architecture

Implementation planning should include tests for:

### Pure domain
- transforms;
- FK;
- IK;
- limits;
- coordinate frames;
- tool TCP;
- point operations.

### Program/source
- parser fixtures;
- comment/trivia preservation;
- round-trip source;
- opaque/direct-code preservation;
- syntax-error retention;
- semantic edits.

### Runtime
- task state transitions;
- waits/timers;
- breakpoint behavior;
- I/O propagation;
- actuator/sensor propagation;
- grasp/release;
- deterministic simulation clock.

### RC+ workspace
- command registry;
- enable/disable state;
- shortcut mapping;
- Project Explorer interactions;
- window-manager transitions;
- form-factor state preservation.

### Persistence
- known resource updates;
- opaque byte preservation;
- sidecar metadata separation;
- conflict detection.

### Adapters
- capability gating;
- School Setup vs Full Learning;
- robot provider loading;
- simulator adapter registry.

### Integration
- source → runtime;
- runtime → I/O/workcell;
- Visual Lab ↔ shared runtime;
- RC+ Trainer ↔ shared runtime.

No test of Local Simulation is evidence that a physical robot is safe to operate.

## 42. Observability and reproducibility

To support debugging and education, the runtime should expose structured state/event traces where practical.

Useful categories:

- program/task events;
- robot motion events;
- I/O changes;
- sensor/actuator changes;
- workcell interaction events;
- diagnostics;
- bridge connection events.

Trace/logging design should avoid exposing sensitive user/project data by default.

## 43. Implementation strategy constraint

This design does **not** authorize immediate implementation.

Next required steps:

1. User reviews/approves this formal spec.
2. Invoke Superpowers writing-plans.
3. Produce an implementation plan with small verifiable tasks.
4. Use TDD where applicable.
5. Verify builds/tests before claiming completion.
6. Implement in phases rather than attempting all RC+ modules at once.

## 44. Recommended implementation order for planning

The eventual implementation plan should likely establish foundations in this dependency order:

1. shared-runtime/domain contracts;
2. robot/provider capability model;
3. command + simulator adapter registries;
4. project/resource persistence model;
5. source-preserving SPEL+ document foundation;
6. task/I/O/simulation clock foundations;
7. RC+ workspace/window manager shell;
8. Project Explorer + program/point windows;
9. core Robot Manager integration;
10. Command Window / I/O Monitor / Task Manager;
11. Visual Lab synchronization with the same runtime;
12. workcell functional actors;
13. optional-module packages;
14. Windows bridge research/implementation as a separate project phase.

This is an architectural dependency recommendation, not yet the implementation plan.

## 45. Acceptance criteria for this architecture

The architecture is successful when:

- RC+ Trainer and Visual Lab cannot diverge because they share one canonical runtime;
- native source can be imported without losing unknown code/comments;
- unsupported source is preserved rather than deleted;
- I/O Monitor reflects the same I/O that drives workcell actors;
- Task Manager reflects the same tasks executing program code;
- Project Explorer and RC+ tools use one command/window registry;
- School Setup and Full Learning differ by capabilities, not duplicated applications;
- a second robot can be added without changing core UI math assumptions;
- a future simulator adapter can be added without turning its language into SPEL+;
- the app can remain fully useful offline;
- RC+ Digital Twin remains architecturally distinct from future real-hardware control.

## 46. Open research items

These are not blockers for approving this architecture, but must be resolved before their implementation phases:

- exact RC+ 7.5.3 school-controller/options inventory;
- positive J1-J6 direction and Cartesian mapping calibration against real RC+;
- exact per-page Robot Manager field/control inventories;
- exact toolbar icon/command availability per profile/version;
- exact native project resource behavior where documentation is incomplete;
- supported/documented Windows integration interfaces for RC+ bridge;
- independent commercial replacement of CAD-derived C4 render mesh;
- future RC+ 8.x profile research;
- second robot/provider selection for extensibility validation.

## 47. Self-review checklist

- [x] Preserves all five approved architecture sections.
- [x] Keeps RC+ fidelity separate from visual copying.
- [x] Uses one canonical runtime for both interfaces.
- [x] Preserves native source and opaque project data.
- [x] Separates Local Simulation, RC+ Digital Twin and future Real Hardware.
- [x] Supports future robots/simulators through adapters.
- [x] Keeps Visual Lab independent from RC+ desktop UI.
- [x] Keeps language adapters native rather than SPEL+-forcing.
- [x] Includes School Setup and Full Learning capability profiles.
- [x] Includes bilingual/contextual-learning architecture.
- [x] Includes asset/IP provenance boundaries.
- [x] Does not authorize production implementation before planning.
- [x] Identifies remaining research without pretending it is resolved.

## 48. Approval history

Approved design sections:
1. Shared Runtime / Single Source of Truth — approved.
2. Program Document + Programming Language Architecture — approved.
3. Workcell / I/O / Task Runtime — approved.
4. RC+ Trainer Workspace / Window / Command Architecture — approved.
5. Persistence / Round-trip / Bridge Architecture — approved on 2026-09-16.

Formal design spec status: **approved by the user on 2026-09-16; implementation planning authorized**.