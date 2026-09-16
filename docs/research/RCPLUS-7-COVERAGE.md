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
