# EPSON RC+ 7.0 Learning Coverage Inventory

Status: research baseline, not implementation-complete.

## Purpose

Track the RC+ Trainer coverage target against official Epson documentation. The goal is to let a learner practice the same concepts, names, window relationships, and workflows they will encounter in EPSON RC+ 7.0 while also exposing optional modules for learning.

Baseline environment confirmed for this project:
- EPSON RC+ 7.0
- School installation: version 7.5.3
- Robot baseline: Epson C4-A601S

## Official source baseline

- EPSON RC+ 7.0 support/manuals page:
  https://epson.com/Support/Robots/Software/Epson-RC%2B-7-0/s/SPT_R12N793031
- EPSON RC+ 7.0 (Ver. 7.5) User's Guide:
  https://files.support.epson.com/far/docs/epson_rc_pl_70_users_guide-rc700_rc90%28v75r9%29.pdf
- Epson Robot Specification Catalog / options:
  https://files.support.epson.com/far/docs/epson_robot_specification_catalog_cpd-54833r4.pdf

## Standard RC+ shell / IDE

Target coverage includes the documented main development-environment structure:
- Main parent application window
- Menu bar
- Tool bar
- Project Explorer
- Status pane/window
- Status bar
- Multiple simultaneously open child windows (MDI-style)
- Window management such as cascade/tile where applicable
- Program/source editors
- Point files and project resources
- Run/operator workflow
- Command Window
- Robot Manager
- I/O Monitor
- Task Manager
- Macros
- I/O Label Editor
- User Error Editor
- Controller-related windows/configuration

## Official top-level menu structure

Target:
- File
- Edit
- View
- Project
- Run
- Tools
- Setup
- Window
- Help

Exact submenu coverage will be inventoried from the official manual before implementation.

## Optional / advanced learning coverage

The RC+ 7.0 User's Guide and Epson option catalog document optional or advanced capabilities including:
- Vision Guide
- RC+ API / .NET support
- Security
- Conveyor Tracking
- PG Motion System / auxiliary axes
- ECP
- GUI Builder
- Force Guide
- Force Control
- Fieldbus I/O
- Parts Feeding / feeding-system integration
- OCR and Add-On Instructions where documented for the relevant RC+ generation/controller ecosystem

This list is a research baseline, not yet a claim that every option appears identically in the user's installed 7.5.3 configuration.

## Learning-state labels

Each optional capability in RC+ Trainer should be marked with metadata such as:
- Standard
- Optional software license
- Optional hardware required
- Controller/robot dependent
- Available on school setup: Yes / No / Unknown
- Implemented in trainer: Not started / Partial / Functional / High-fidelity

## Product rule

RC+ Trainer should allow learning optional modules even when the real school installation does not have them. The UI must clearly distinguish simulated learning availability from actual license/hardware availability on a real Epson controller.

## Verified shell behavior from official RC+ 7 documentation

The official RC+ 7 User's Guide confirms the following behavior for the desktop development environment:

- RC+ uses an MDI-style main environment with multiple child windows.
- Project Explorer remains alongside the child-window workspace.
- Standard top-level menus are: File, Edit, View, Project, Run, Tools, Setup, Window, Help.
- The Window menu manages open child windows and includes at least Cascade and Tile Vertical behaviors.
- Robot Manager can be configured as either:
  - an MDI child window; or
  - a foreground dialog.
- Robot Manager shortcut: F6.
- Command Window shortcut: Ctrl+M.
- I/O Monitor shortcut: Ctrl+I.
- Task Manager shortcut: Ctrl+T.

### Verified Tools menu baseline

The RC+ 7.0 manual documents these standard Tools entries:
- Robot Manager
- Command Window
- I/O Monitor
- Task Manager
- Macros
- I/O Label Editor
- User Error Editor
- Controller

### RC+ Trainer implication

The Android trainer should model these as distinct windows/tools inside the RC+ workspace rather than flattening them into a mobile dashboard. Touch adaptations may modernize window chrome and controls, but opening, focusing, switching, arranging, and using tools should teach the same mental model as RC+.

Source references:
- EPSON RC+ 7.0 User's Guide, GUI / Tools / Window menu sections.
- Official Epson support PDF: https://files.support.epson.com/far/docs/epson_rc_pl_70_users_guide_spanish_%28v73r2%29.pdf
- Official Epson support PDF: https://files.support.epson.com/far/docs/r6.pdf

## Verified keyboard shortcuts baseline

Current verified examples from official RC+ 7 documentation:
- Robot Manager: F6
- Command Window: Ctrl+M
- I/O Monitor: Ctrl+I
- Task Manager: Ctrl+T

The complete shortcut inventory must be extracted from official RC+ 7 documentation and tracked here before implementation. Only verified mappings should be presented to learners as official RC+ shortcuts.

## Verified RC+ 7.0 project/file model

Official RC+ 7.0 v7.5 documentation establishes the following project structure and behavior:

- Each project has a generated project file with extension **.sprj**.
- Program/source files use **.prg** and contain SPEL+ functions.
- Include files use **.inc**.
- Robot point files use **.pts**.
- Imported macro files use **.mac**.
- Project-scoped I/O labels and user errors are managed as project data/resources.
- A file can exist physically in the project folder without being part of the project build tree; it must be added to the project before normal editing/use through the project.
- Project > Edit manages which program/include/point files are included in the project build tree.
- File > New creates Program, Include, or Point files.
- File > Open opens project Program/Include/Point files; Ctrl+O is documented.
- Project Explorer presents current project files/functions as a sorted tree and supports opening a file or jumping to a function by double-clicking.
- Project Explorer supports contextual actions for project-tree items.
- The Project Explorer pane can be hidden/shown, resized, and placed on the left or right side in RC+ 7.0.
- The Status pane is bottom-docked in RC+ 7.0 and automatically reopens when important build/error output needs to be shown.
- A default point file can be assigned per robot; common point files can also be loaded explicitly from SPEL+.

### RC+ Trainer implication

The Trainer should model a **real project model**, not a generic Android file browser. The Project Explorer is a view of the active RC+ project/build structure, while Android storage/import/export is a separate adapter layer.

Sources:
- Epson RC+ 7.0 support/manual page: https://epson.com/Support/Robots/Software/Epson-RC%2B-7-0/s/SPT_R12N793031
- Epson RC+ 7.0 v7.5 User's Guide, Project Explorer / File / Project sections.

## Verified Project Explorer interaction baseline

From the official EPSON RC+ 7.0 User's Guide:

- Project Explorer displays project files and functions in a sorted tree.
- Double-clicking a file opens it.
- Double-clicking a function jumps directly to that function.
- The pane can be hidden/shown from View > Project Explorer.
- The pane can be resized.
- The pane can be docked on either the left or right side of the main window.
- Right-clicking a project-tree item opens a context menu.
- Verified context-menu commands shown in the manual:
  - New...
  - Open
  - Rename...
  - Remove
  - Delete
- The Status pane is bottom-docked, resizable vertically, and automatically reopens when an important error/status message must be shown.

Android design consequence:
- Keep the same tree/action model and command names.
- Use our own icons and visual styling.
- A touch equivalent for right-click still needs explicit product approval.

Official sources:
- https://files.support.epson.com/far/docs/r6.pdf
- https://files.support.epson.com/far/docs/epson_rc_pl_70_users_guide_spanish_cpd60416_%28v75r1%29.pdf

## Fidelity research operating rule

The user has approved a blanket fidelity rule: once an RC+ behavior is verified from official documentation or the confirmed school environment and it materially helps learning transfer, it should be carried into RC+ Trainer without requiring another yes/no decision for every small detail.

Research should therefore focus on building a complete verified inventory of:
- menus/submenus;
- toolbars and shortcuts;
- Project Explorer/project files;
- editors and point files;
- Robot Manager;
- I/O Monitor;
- Task Manager;
- Command Window;
- Controller/setup;
- build/run/status/error behavior;
- optional modules;
- MDI/window behavior;
- other documented workflows.

Escalate for product approval only when fidelity conflicts with Android usability, legal/IP boundaries, safety, hardware reality, or a significant design fork.

## Verified Robot Manager baseline for C4 / 6-axis learning

Official EPSON RC+ 7.0 v7.5 documentation shows Robot Manager as a substantial multi-page tool, not a simple jog card.

### Control Panel
Verified functions/status:
- Robot selector
- Emergency Stop status
- Safeguard status
- Motors status
- Power status
- MOTOR OFF / MOTOR ON
- POWER LOW / POWER HIGH
- Reset
- Home
- Free/Lock joint controls where supported by the robot type

For a 6-axis C4, unsupported controls must follow RC+ enable/disable behavior rather than being invented.

### Jog & Teach
Verified controls/behavior:
- Robot / Local / Tool / ECP selectors
- Jog modes: World, Tool, Local, Joint, ECP
- Speed selector
- Cartesian jog axes X/Y/Z/U/V/W for 6-axis robots
- Joint jog controls in Joint mode
- Current Position views: World / Joint / Pulse
- Current Arm Orientation fields such as Hand, Elbow, Wrist and relevant flags
- Jog Distance modes: Continuous, Long, Medium, Short
- Teach Points tab
- Execute Motion tab
- Point file and point selector
- Teach and Edit actions
- Singularity/step-jog warning behavior documented by RC+ should be represented in the simulator where applicable

### Points
Verified behavior:
- Point File selector
- Point spreadsheet
- Columns for Number, Label, X, Y, Z, U, V, W for the C4-class 6-axis example
- Delete selected point
- Delete All
- Save
- Restore
- Teaching from Jog & Teach updates the Points view
- In MDI mode, Ctrl+S saves point data

### Additional Robot Manager pages visible/documented for the C4-class environment
Official v7.5 screenshots/manual sections show a page list including:
- Hands
- Arch
- Locals
- Tools
- Pallets
- ECP
- Boxes
- Planes
- Weight

Older/specific controller/robot configurations also document pages such as:
- Arms
- Inertia
- XYZ Limits
- Range

Page availability is robot/controller/option dependent and must therefore be data-driven rather than hard-coded as universally available.

### Arch
Verified:
- Arch table with seven setting pairs
- Depart Z
- Approach Z
- Apply
- Restore
- Defaults
- Clear
- Used by Jump / Jump3 / Jump3CP motion behavior

### Optional Force integration
When Force Guide is available, official documentation shows a Force page integrated into Robot Manager with panels such as:
- Control
- Trigger
- Coordinate System
- Monitor
- Motion Restriction

This belongs to Full Learning and appears in School Setup only if verified as present/licensed.

## Design consequence

Robot Manager in RC+ Trainer must be implemented as a page registry driven by robot/controller/options/profile capability metadata. The window shell stays the same, while the left-page list and controls reflect the selected robot and installed/learning capabilities.

Sources:
- EPSON RC+ 7.0 User's Guide Rev.9: https://files.support.epson.com/far/docs/epson_rc_pl_70_users_guide-rc700_rc90%28v75r9%29.pdf
- EPSON RC+ 7.0 Spanish User's Guide: https://files.support.epson.com/far/docs/epson_rc_pl_70_users_guide_spanish_%28v73r2%29.pdf
- Force Guide 7.0 manuals from Epson support.

## Verified RC+ 7.0 v7.5 menu inventory

The official v7.5 documentation confirms this baseline menu structure. Section numbering varies across manual revisions, so commands are tracked by menu/name rather than chapter number.

### File
- New
- Open
- Close
- Save
- Save As
- Restore
- Rename
- Delete
- Import
- Print
- Exit

### Edit
- Undo
- Redo
- Cut
- Copy
- Paste
- Find
- Find Next
- Replace
- Select All
- Indent
- Outdent
- Comment Block
- Uncomment Block
- Go To Definition
- Navigate Backward (documented in later v7.5 revisions)

Verified shortcut examples:
- Undo: Ctrl+Z
- Redo: Ctrl+Y
- Cut: Ctrl+X
- Copy: Ctrl+C
- Paste: Ctrl+V
- Find: Ctrl+F
- Find Next: F3
- Replace: Ctrl+R
- Select All: Ctrl+A

### View
- Project Explorer
- Status Window
- System History

### Project
- Wizard (present in later v7.5 documentation)
- New
- Open
- Recent Projects
- Close
- Edit
- Save
- Save As
- Rename
- Import
- Export
- Copy
- Delete
- Build
- Rebuild
- Properties

Verified build shortcuts:
- Build: Ctrl+B
- Rebuild: Ctrl+Shift+B

### Run
- Run Window
- Operator Window
- Step Into
- Step Over
- Walk
- Resume
- Stop
- Toggle Breakpoint
- Clear All Breakpoints
- Display Variables
- Call Stack

Verified shortcuts:
- Run Window: F5
- Operator Window: Shift+F5
- Step Into: F11
- Step Over: F10
- Walk: F12
- Resume: F7
- Toggle Breakpoint: F9
- Clear All Breakpoints: Ctrl+Shift+F9
- Display Variables: F4

### Tools
Core documented entries:
- Robot Manager
- Command Window
- I/O Monitor
- Task Manager
- Macros
- I/O Label Editor
- User Error Editor
- Controller

Option/profile-dependent Tools entries documented in v7.5 materials can include capabilities such as:
- Vision / Vision Guide
- Force-related tools such as Force Monitor
- Simulator-related tools and optional modules

The Tools menu must therefore be capability/profile driven.

### Setup
- PC to Controller Communications
- System Configuration
- Preferences
- Options

### Window
- Cascade
- Tile Vertical
- Tile Horizontal
- Arrange Icons
- Close All
- numbered/currently-open-window list
- Windows

The Windows dialog supports operations such as Activate, Save and Close for selected windows.

### Help
- How Do I
- Contents
- Index
- Search
- Manuals
- About EPSON RC+ 7.0

## Verified Build / Run / Status behavior

### Build
- Project > Build performs the minimum compile/link/update work needed to bring the project/controller up to date.
- Build progress is shown in the Status pane.
- Build errors are shown in the Status pane.
- Rebuild recompiles/relinks the entire project and sends project point files as documented.
- Build uses Ctrl+B; Rebuild uses Ctrl+Shift+B.

### Run Window / Operator Window
- Opening Run Window saves changed files as needed, builds the project, and only opens the Run Window when the build succeeds.
- When Auto File Save is disabled, RC+ prompts to save changed files before build/run.
- Run Window is primarily for testing/debugging.
- Operator Window is intended as a simpler operator interface.
- Run Window shortcut is F5; Operator Window is Shift+F5.

### Debugging
- Breakpoints can be toggled from the Run menu, F9, or editor margin where supported.
- When a breakpoint is reached, RC+ opens the source window and highlights the execution line.
- Step Into, Step Over and Walk preserve their documented debugging semantics.
- Resume is available for halted tasks.
- Stop stops running tasks.

### Status pane / status bar
- Status pane is bottom-docked and vertically resizable.
- If closed, it automatically reopens when an important build/error message needs to be shown.
- Build error entries include source context and can be double-clicked to navigate to the relevant source line.
- Status bar includes message/status areas including operation-mode and emergency-stop state in documented configurations.

### RC+ Trainer consequence
RC+ Trainer should preserve these workflows and state transitions, not merely display matching menu names. Commands should enable/disable based on the same conceptual state (active document, dirty files, build state, running/halted tasks, controller/profile capability), with simulation-safe equivalents in Local Simulation mode.

Official baseline:
- EPSON RC+ 7.0 User's Guide v7.5 (official Epson support/download).
