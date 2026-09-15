# Development Roadmap

## Phase 0 — Foundation
- repository;
- architecture;
- Android project;
- product decisions;
- placeholder trainer UI.

## Phase 1 — 3D Robot Viewer
- load first Epson model;
- camera orbit/pan/zoom;
- link/joint node mapping;
- selectable joints;
- world/base/TCP frames.

Exit: robot can be inspected intuitively.

## Phase 2 — Joint Simulation
- RobotDefinition for exact model;
- forward kinematics;
- joint controls;
- limits;
- home pose.

Exit: moving J1...Jn moves the 3D robot correctly.

## Phase 3 — Touch TCP + IK
- TCP handles;
- screen/axis/plane drag;
- position IK;
- full pose IK;
- ghost pose;
- reach/limit/singularity diagnostics.

Exit: user can drag the end effector naturally.

## Phase 4 — Teach Points
- save P1/P2/...;
- edit/rename/delete;
- visualize targets;
- point table;
- persistence.

## Phase 5 — First Functional Tool
- two-finger gripper;
- attach/detach;
- TCP change;
- open/close state;
- graspable object.

Exit: simulated pick and place works.

## Phase 6 — Programming
- program action model;
- visual/code-like editor;
- line-by-line execution;
- Go/Move;
- tool actions;
- speed/wait;
- diagnostics.

## Phase 7 — Additional Tools
- vacuum;
- welding;
- hand;
- custom tool definition.

## Phase 8 — Workcell
- tables/fixtures;
- parts;
- pallets;
- conveyors;
- collision checks;
- saved cell projects.

## Phase 9 — Learning Mode
- interactive lessons;
- guided challenges;
- scoring/progress;
- explain current joint/TCP concepts.

## Phase 10 — Optional Epson Bridge
- Windows bridge prototype;
- connection to Epson simulator/software only after protocol/API verification;
- read-only telemetry first;
- simulated command bridge after safety review.

Physical robot control is a separate future project gate, not an automatic extension of simulation mode.
