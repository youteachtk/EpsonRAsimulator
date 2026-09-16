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

### Startup and resume behavior
- On the **first launch** or when there is no active project/work in progress, the home screen presents two large primary entries:
  - **RC+ Trainer**
  - **Visual Lab**
- Once a project/session has active work, subsequent launches should **resume directly into the last active view/mode** instead of forcing the user through the mode chooser every time.
- The user must still be able to switch between RC+ Trainer and Visual Lab at any time from a clear in-app mode switcher.
- The last-view resume behavior must never hide or duplicate project state: both experiences continue operating on the same current project/simulation state.

### RC+ Trainer learning availability
- RC+ Trainer should expose the **full set of implemented RC+-like areas from the beginning** rather than hiding or locking advanced screens behind progression.
- Guided learning is an **optional overlay**, not a gate. A learner can enter any available screen at any time.
- The product is intended for self-directed learning as well as guided practice; the user should be able to explore unfamiliar RC+ areas freely and use contextual help when needed.
- Each RC+-like area should support contextual **? help**, short explanations of purpose and controls, and optional guided walkthroughs/tasks.
- Guided mode may recommend an order of learning, highlight the next control to use, and provide practice exercises, but it must never remove access to the underlying screen or simplify away the real concept.

### Bilingual learning and translation
- Tutorials, guided walkthroughs, contextual help, and educational explanations must be available in **both English and Spanish**.
- The learner can switch language without leaving the current screen or losing progress.
- **English is the reference terminology** for RC+-specific labels, SPEL+ keywords, commands, and official Epson concepts so learners see the same vocabulary they will encounter in EPSON RC+.
- Spanish translation/explanation is provided alongside or on demand for accessibility to classmates who are less comfortable in English.
- Translation must never alter code, command names, point names, I/O identifiers, or other technical tokens that must remain exact.
- Where an Epson/RC+ term does not have a safe one-to-one translation, keep the original English term and explain it in Spanish instead of inventing a misleading replacement.

### Full-app bilingual interface
- The **entire application** must support both **English and Spanish**, not only tutorials and contextual help.
- This includes RC+ Trainer, Visual Lab, navigation, settings, dialogs, simulator controls, workcell controls, programming UI, points, I/O panels, diagnostics, lessons, and general app text.
- Users may switch language without leaving the current project or losing simulation/program state.
- RC+/SPEL+ technical tokens, source code, identifiers, point names, I/O names, and other exact machine/program symbols must remain unchanged by translation.
- In **RC+ Trainer**, verified official Epson/RC+ terminology should remain recognizable and faithful to the official software; Spanish may appear as a translated label or explanation where that does not alter the technical token.
- In **Visual Lab**, the full UI may be naturally localized into either English or Spanish because it is the app's own interface.
- Translation architecture must be centralized so new screens and features are bilingual by default rather than translated later as an afterthought.

### RC+ Trainer fidelity target
- RC+ Trainer is no longer defined as merely "RC+-like". Its goal is **functional and structural fidelity to EPSON RC+ 7.0**, using the school's confirmed EPSON RC+ 7.0 v7.5.3 environment as the baseline reference.
- The trainer should reproduce the **complete standard development-environment mental model**: main parent window, menu bar, tool bar, Project Explorer, Status Window/Pane, status bar, and multiple simultaneously open child windows.
- The RC+ Trainer workspace should behave as an **MDI-style environment**. Program editors and tool windows can be opened together, focused, moved/resized where practical on Android, and arranged using equivalents of RC+'s Window commands (including cascade/tile behavior where appropriate).
- The main menu architecture should preserve the verified RC+ top-level structure: **File, Edit, View, Project, Run, Tools, Setup, Window, Help**.
- Implemented RC+ tools should open as their own internal windows/panels rather than being flattened into one simplified dashboard. This includes, as applicable and verified: **Robot Manager, Command Window, I/O Monitor, Task Manager, Macros, I/O Label Editor, User Error Editor, Controller**, program/source windows, point files, Run/Operator windows, and other documented RC+ windows.
- Robot Manager must be treated as a substantial RC+ subsystem with its own pages/tabs and behavior, not as a small custom robot-control card.
- The goal is to let a learner practice navigation and workflows in RC+ Trainer and then recognize the same concepts, names, window relationships, and procedures when using EPSON RC+ on Windows.
- Fidelity is **functional/structural rather than pixel-copying**. Visual styling may be modernized for Android: cleaner typography, rounded controls/windows, improved spacing, touch targets, responsive layout, and richer 3D visualization, while preserving RC+ organization and behavior.
- The 3D robot icon used in early HTML mockups is only a placeholder. Production RC+ Trainer and Visual Lab must use the actual articulated Epson C4-A601S 3D model.
- Visual Lab remains a separate, more original interface and is not constrained by RC+'s desktop layout.

### Full RC+ learning coverage, including optional modules
- RC+ Trainer should ultimately teach **all documented EPSON RC+ 7.0 areas and optional modules that are relevant to the supported controller/robot ecosystem**, not only the subset installed or licensed on the school's PC.
- Optional/licensed/hardware-dependent capabilities remain accessible in the trainer for learning, but must be clearly identified as **optional / license-dependent / hardware-dependent** where applicable.
- The trainer should distinguish:
  - what is part of the standard RC+ environment;
  - what requires an optional software license;
  - what requires additional controller hardware, sensors, cameras, force sensors, fieldbus, conveyor hardware, teach pendant, or other equipment;
  - what is present on the user's school setup versus what is available only in the full-learning catalog.
- Learning availability in the trainer is not the same thing as claiming the real school controller has that option installed.
- Coverage should be built from official Epson RC+ 7.0 documentation and option manuals, with a living feature inventory in the repository.

### RC+ Trainer profiles: School Setup and Full Learning
- RC+ Trainer will provide two learning profiles over the same RC+ fidelity architecture:
  - **School Setup**: reproduce the capabilities, modules, controller/robot context, and availability of the user's actual school installation as closely as verified.
  - **Full Learning**: expose the full documented RC+ learning catalog, including optional/license/hardware-dependent modules, for study and simulation.
- Full Learning availability must not imply that those options are licensed or physically installed on the school controller.
- The profile choice affects availability/learning context, not the underlying fidelity requirement for RC+ concepts and workflows.

### Robot 3D asset strategy for commercialization
- Do **not** contact Epson for permission at this stage.
- The preferred long-term commercial path is to build a **new independently created C4-A601S-compatible visual model** using our own modeling work, measurements, photographs, dimensional references, and other lawfully usable factual information.
- The independent model should preserve the robot's recognizable proportions, joint layout, mounting points, kinematic pivots, envelopes, and simulator usefulness while using independently authored mesh/topology/materials/textures.
- The current CAD-derived C4 model remains the **development/reference fallback** because its visual quality is already approved by the user.
- If the independent replacement cannot achieve acceptable visual/technical quality, the current model may continue to be used for private development/testing; however, its commercial redistribution status remains unresolved and it must not silently be treated as commercially cleared.
- Kinematics, pivots, joint axes, collision proxies, TCP/flange data, and animation behavior must be kept separate from the render mesh so the visual model can be replaced without rewriting robot logic.
- Asset provenance must be documented for every future robot/tool/workcell model.

### RC+ Trainer form-factor behavior
- **Landscape tablet is the primary RC+ Trainer form factor** and should present the closest approximation to the full RC+ desktop workspace.
- In landscape tablet mode, preserve the MDI mental model: menu bar, toolbar, Project Explorer, status areas, and multiple simultaneously visible child windows.
- Phone and portrait layouts are **adaptations of the same RC+ workspace**, not separate simplified products.
- On smaller screens, a child window may maximize automatically for usability, while an open-window switcher/task strip provides fast access to the rest of the active RC+ windows.
- Switching device orientation or screen class should preserve the same project, open tools/windows, active document, and window state wherever practical.
- Returning to a larger landscape workspace should restore multi-window MDI layout instead of losing the user's working context.

### RC+ Trainer internal window taskbar
- RC+ Trainer child windows are fully windowed and support **move, resize, maximize, restore, minimize, focus, and close** inside the RC+ workspace.
- Minimizing a child window sends it to an **internal taskbar/window bar** inside RC+ Trainer rather than simply hiding it.
- The internal taskbar shows currently minimized/open RC+ child windows and allows one-tap restore/focus.
- Window state (position, size, minimized/maximized state, z-order/focus where practical) should persist across orientation and screen-size changes.
- RC+-style Window commands such as Cascade/Tile operate on non-minimized child windows; minimized windows remain available in the internal taskbar.
- On phone/small layouts, the same taskbar concept becomes the compact open-window switcher rather than removing window-management behavior.

### RC+ Trainer workspace is an embedded desktop, not an operating system
- RC+ Trainer intentionally uses a **desktop/window-manager metaphor** because EPSON RC+ itself is a multi-window desktop development environment.
- This is **not** a separate operating system and should not duplicate Android concepts such as apps, notifications, system settings, filesystems, or a general-purpose launcher.
- The embedded workspace exists only to reproduce RC+ behaviors: child windows, focus/z-order, move/resize, maximize/restore, minimize, internal taskbar, Cascade/Tile, Project Explorer, tool windows, and status areas.
- The internal taskbar belongs only to RC+ Trainer and should represent RC+ windows/tools, not unrelated app features.
- Visual Lab remains a normal modern Android experience and does not inherit the desktop metaphor unless a specific tool benefits from it.

### RC+ Trainer taskbar and authentic tool entry points
- The internal RC+ taskbar is **only for minimized child windows**. It is not a launcher and does not contain permanent shortcuts to RC+ tools.
- RC+ tools such as Robot Manager, I/O Monitor, Command Window, Task Manager, and other utilities must be opened from the **same conceptual locations used by RC+** (menus and toolbars, once verified), so the learner practices authentic navigation.
- The taskbar appears as a window-management aid only and should remain visually subordinate to the RC+ workspace.
- Child-window touch behavior should support:
  - drag from title bar to move;
  - double-tap title bar to maximize/restore;
  - resize from edges/corners using larger invisible touch targets than the visible border;
  - minimize to the internal taskbar;
  - restore/focus with one tap from the taskbar.
- These Android adaptations may improve usability but must not replace RC+ menu/tool workflows.

### RC+ keyboard shortcut fidelity
- RC+ Trainer should reproduce **verified EPSON RC+ keyboard shortcuts** when a physical, Bluetooth, or hardware keyboard is connected to the Android device.
- Shortcuts are part of the learning target: using a shortcut in RC+ Trainer should open/focus/execute the same conceptual tool or command as in RC+, where documented and safe.
- Touch controls remain fully available; keyboard support augments rather than replaces the Android interaction model.
- Shortcut handling must be contextual and must not override Android/system-reserved key combinations.
- Only shortcuts verified against official RC+ documentation for the supported version/profile should be labeled as official RC+ shortcuts.
- The shortcut map should be centralized and data-driven so School Setup and Full Learning can expose the appropriate verified commands.

### RC+ names/labels vs original visual assets
- Preserve **verified RC+ functional names, menu labels, tool/window names, shortcuts, file extensions, and SPEL+ command names** where necessary for authentic learning and interoperability.
- Do **not** copy Epson/RC+ icons, logos, screenshots, proprietary artwork, exact graphic assets, or pixel-for-pixel visual styling.
- RC+ Trainer uses an **independently designed icon set and visual skin** while retaining the functional terminology and navigation model learners must recognize.
- Brand names such as EPSON / EPSON RC+ are used only descriptively for training/compatibility context and must not be used in a way that implies official sponsorship or affiliation.


### RC+ project round-trip preservation
- Imported RC+ projects should be **round-trip preserving whenever technically possible**.
- Files/configuration that the app does not understand must be preserved byte-for-byte or as untouched opaque project resources rather than discarded or rewritten.
- Known editable resources such as .prg/.inc/.pts may be represented in the app, but unsupported sections/data must survive export.
- Unknown/proprietary project structures such as .sprj must not be guessed or destructively regenerated. Preserve originals until the format/behavior is independently verified.
- Android/Visual Lab metadata should live in separate sidecar/app metadata so it does not corrupt native RC+ project content.

### RC+ Trainer mouse/touch context menus
- Context menus in RC+ Trainer preserve the desktop RC+ interaction model while adding a touch equivalent.
- With a mouse/trackpad, **right-click** opens the contextual menu for Project Explorer items and other RC+ elements where the official environment uses a context menu.
- With touch, **long-press** opens the same contextual menu.
- The menu commands, labels, enable/disable state, and target semantics should match verified RC+ behavior; only the visual styling/icons are independently designed.
- Long-press is an Android accessibility/adaptation layer and must not replace the official mouse behavior when a pointing device is connected.

### RC+ Trainer Project Explorer selection/open behavior
- Project Explorer should preserve the RC+ desktop selection/open model while adapting it to touch.
- **Single click/tap** selects an item without opening it.
- **Double-click/double-tap** opens the selected file or jumps to the selected function, matching verified RC+ behavior.
- Right-click/long-press continues to open the context menu.
- Touch adaptation must not collapse selection and open into the same gesture because the learner should practice the same interaction distinction used in RC+.

### Global RC+ fidelity default
- From this point forward, **preserve verified RC+ behavior by default whenever doing so helps the learner transfer skills to the real EPSON RC+ environment**.
- This blanket approval applies to verified:
  - menu names and menu structure;
  - tool/window names;
  - file/project workflows;
  - editor behavior;
  - open/save/build/run workflows;
  - selection, double-click, right-click and context-menu behavior;
  - MDI window behavior and Window-menu commands;
  - Project Explorer behavior;
  - Robot Manager structure and workflows;
  - I/O, Task Manager, Command Window, point-management, controller/setup and related RC+ workflows;
  - keyboard shortcuts;
  - status/error/build feedback;
  - other interaction semantics that materially teach the real RC+ workflow.
- Do **not** repeatedly ask for approval on each small fidelity detail once official RC+ behavior has been verified.
- Ask the user only when:
  - official RC+ behavior is ambiguous or version-dependent;
  - Android requires a meaningful adaptation that could change how the workflow is learned;
  - there is a legal/IP, safety, hardware, or commercialization tradeoff;
  - multiple materially different implementation choices exist.
- Fidelity approval does **not** authorize copying proprietary visual assets. Icons, logos, screenshots, artwork, window chrome, typography and visual styling remain independently designed.
- When Android needs an adaptation (for example long-press for right-click), preserve the original RC+ interaction when mouse/keyboard is available and add the touch equivalent alongside it.

### Approved top-level architecture: shared neutral runtime (Option B)
- Approved approach: **one neutral shared Project/Simulation Runtime with two separate user interfaces: RC+ Trainer and Visual Lab**.
- RC+ Trainer is a high-fidelity learning interface over the shared runtime. It may reproduce verified RC+ workflows without owning the robot/project/program state.
- Visual Lab is an independent touch-first interface over the same runtime and is free to use a more modern interaction model.
- Both interfaces operate on the same canonical project/program/points/I/O/robot/workcell/task state; they are not synchronized by copying between separate models.
- A change made through one interface becomes visible to the other because both observe the same underlying state.
- The future Windows RC+ Bridge also connects at the shared runtime/integration boundary rather than coupling directly to either UI.
- This architecture is intended to prevent RC+ desktop constraints from leaking into Visual Lab while also preventing divergent project state between the two experiences.

### Extensible multi-robot / multi-simulator platform
- The C4-A601S + EPSON RC+ 7.0 v7.5.3 implementation is the **first reference implementation**, not the permanent product boundary.
- The architecture must allow the training platform to be expanded later with:
  - additional Epson robot models;
  - other Epson controller/RC+ versions or simulator configurations;
  - additional robot simulators and, where technically/documentarily/legal feasible, other manufacturers.
- Expansion is allowed only when there is enough reliable information to build an honest simulator/trainer: official/public documentation, kinematic and controller data, programming/workflow references, and lawful 3D assets or sufficient dimensional/visual information to create an independent model.
- Never claim high-fidelity support for a robot/simulator when the required behavior or data has not been verified.
- Manufacturer/simulator-specific behavior must live behind adapters/providers instead of being hard-coded into the shared simulation runtime.
- A new robot should be addable through a data-driven robot package containing, as applicable:
  - kinematic chain and joint types;
  - joint limits, velocities and other verified motion data;
  - base/flange/TCP frames;
  - home/calibration poses;
  - render model + collision geometry + provenance;
  - tool-mount information;
  - robot/controller capability metadata.
- A new simulator/training environment should be addable through a simulator adapter/profile containing, as applicable:
  - menus, windows, tool/workflow definitions and shortcuts;
  - project/file-format adapters;
  - programming-language parser/runtime/diagnostics adapters;
  - simulator/controller capability metadata;
  - optional bridge/integration adapter.
- Source-specific project formats and programming languages must remain preserved. Cross-simulator conversion is never assumed to be lossless unless an explicit, tested semantic mapping exists.
- The current shared runtime remains the common robot/workcell/I-O/task foundation wherever concepts are portable; vendor-specific semantics stay in adapter modules.
- Future robot/model assets must follow the same provenance/IP rule as the C4: use redistributable assets or independently authored geometry rather than silently redistributing proprietary CAD.
- Existing C4-A601S / RC+ projects must remain compatible as the catalog grows.

### Approved design section 1: Shared Runtime / Single Source of Truth
- The shared-runtime architecture section is approved.
- RC+ Trainer and Visual Lab are two interfaces over one canonical project/simulation state.
- The shared runtime owns project, program, points, robot state, kinematics/motion, tasks, I/O, workcell, tools/TCP, diagnostics, profiles/capabilities and other portable simulation state.
- UIs issue commands/actions to the runtime and observe resulting state; they do not maintain independent authoritative copies.
- School Setup and Full Learning are capability/profile configurations over the same runtime.
- The architecture remains extensible through RobotProvider / SimulatorAdapter / ProgrammingLanguageAdapter / ProjectFormatAdapter boundaries so future robots/simulators can be added without redefining the application core.
