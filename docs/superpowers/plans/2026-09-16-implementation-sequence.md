# RC+ Trainer / Visual Lab Implementation Sequence

**Approved spec:** `docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`

The approved design spans multiple independently testable subsystems. Per the Superpowers writing-plans scope rule, implementation is split into separate executable plans instead of one giant plan.

## Sequence

1. **Shared Runtime Foundation**
   - Neutral robot/provider registry.
   - Capability/profile primitives.
   - Simulator/language/project-format adapter contracts and registry.
   - Canonical runtime state/commands.
   - Migrate the current C4 screen off local authoritative joint state.
   - Result: the existing C4 simulator still works, but its robot state is owned by the shared runtime and the UI no longer hard-codes the Epson catalog directly.
   - Executable plan: `docs/superpowers/plans/2026-09-16-shared-runtime-foundation.md`

2. **Source-Preserving SPEL+ + Native Project Resource Foundation**
   - ProgramDocument with exact source preservation.
   - Token/trivia/concrete-syntax layer.
   - semantic nodes for an intentionally small verified SPEL+ subset;
   - Direct Code / opaque region preservation;
   - syntax diagnostics without destroying the last valid semantic model;
   - native project-resource abstractions and .prg/.inc/.pts fixtures.
   - Exit criterion: import → supported edit → export preserves untouched text/comments/opaque regions.

3. **Task / I-O / Simulation Clock Foundation**
   - deterministic simulation clock;
   - TaskRuntime state transitions;
   - breakpoints/step/resume/stop primitives;
   - shared digital I/O state and labels;
   - program execution actions over the same runtime.
   - Exit criterion: a test program can Wait on an input, resume, set an output, and be observed by TaskRuntime and IoRuntime from the same canonical state.

4. **Functional Workcell + Tool Runtime**
   - component-based workcell entities;
   - sensors, actuators, signal bindings, graspable parts;
   - two-finger gripper first;
   - collision/grasp relationship primitives;
   - auxiliary-axis-ready component contract.
   - Exit criterion: virtual sensor → input → task → output → cylinder/gripper → part-state chain works deterministically in tests and 3D.

5. **RC+ Trainer Workspace Foundation**
   - RcCommandRegistry;
   - RcToolRegistry;
   - RcWindowManager;
   - menu/toolbar/Project Explorer/Status structural shell;
   - internal MDI child-window lifecycle;
   - tablet landscape + phone adaptation;
   - verified mouse/touch/keyboard entry-point semantics.
   - Exit criterion: the RC+ Trainer shell opens multiple registered internal windows from menu/toolbar/shortcut through one command model and preserves window state across layout changes.

6. **Core RC+ Windows**
   - Project Explorer + source/point documents;
   - Robot Manager baseline;
   - Command Window;
   - I/O Monitor;
   - Task Manager;
   - Build/Run/Status foundations.
   - Exit criterion: those windows are live views/controllers over the shared runtime, not mock screens.

7. **Visual Lab Migration + Shared Programming View**
   - preserve/improve the current 3D experience;
   - direct shared state with RC+ Trainer;
   - visual ↔ semantic program representation for supported SPEL+;
   - Teach Points shared with .pts/Robot Manager.
   - Exit criterion: changing a point/joint/program action in either experience is immediately reflected in the other through the same runtime.

8. **Persistence + Round-Trip**
   - project resource store;
   - NativeKnownEditable / NativeKnownPreserved / NativeOpaque / AppSidecarMetadata;
   - byte-preserving unknown-resource export;
   - local autosave and session/workspace restore;
   - source/project conflict model.
   - Exit criterion: project import/edit/export tests prove untouched opaque resources survive exactly and app metadata never pollutes native files.

9. **RC+ Digital Twin Bridge Research + Protocol**
   - first verify Epson-supported Windows interfaces from official documentation;
   - define bridge protocol separately from Android runtime;
   - project synchronization and live synchronization as different channels;
   - external-authority command acknowledgements and conflict semantics.
   - Exit criterion: protocol tests with a fake Windows bridge; no physical robot control.

10. **High-Fidelity Coverage + Full Learning**
    - page-by-page RC+ field verification;
    - School Setup capability package;
    - Full Learning capability packages;
    - Vision/Force/GUI Builder/Conveyor/Part Feeding/etc. only after documented behavior is sufficiently verified;
    - bilingual contextual help.

11. **Second Robot / Simulator Extensibility Validation**
    - choose a second robot with reliable documentation and lawful asset path;
    - load it through RobotProvider without core C4 changes;
    - later choose a second simulator ecosystem to validate SimulatorAdapter and ProgrammingLanguageAdapter boundaries.

12. **Commercialization Readiness**
    - independent/commercially cleared C4 render asset;
    - final product brand;
    - packaging/licensing;
    - privacy/cloud review if cloud features exist;
    - pre-launch IP/legal review.

## Execution policy

- Production work uses an isolated branch/worktree.
- Each executable plan uses TDD for pure domain behavior.
- Each task ends in a focused commit and fresh verification.
- SceneView remains pinned at `4.35.0` unless a separate verified dependency change is approved.
- Local Simulation and RC+ Digital Twin never imply physical robot safety/control.
- Later detailed plans are written against the actual APIs produced by prior phases rather than guessing file names and signatures months in advance.
