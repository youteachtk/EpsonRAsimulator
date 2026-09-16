# Master Creation Plan

## Product goal

Create an Android application that teaches the operation and programming concepts of Epson industrial robots through a visual, touch-first simulator.

The learner should be able to understand what the robot will do before dealing with a real machine.

## Non-negotiable product principles

1. Visual first: robot motion must be understandable from the 3D view.
2. Touch first: the TCP can be dragged directly.
3. Learn by doing: controls explain the robot while the learner manipulates it.
4. Safe by architecture: simulation is isolated from any future real-hardware connection.
5. Modular robot definitions: support additional Epson models without rewriting the app.
6. Modular tools: grippers, hands, welders and custom tools are pluggable modules.
7. Explain the math: joint values and Cartesian coordinates are visible during motion.
8. Preview before motion: target/ghost robot can be shown before applying a simulated move.
9. Programming is visual: code and robot motion remain synchronized.
10. Preserve project decisions in this repository.

## Product experiences

### RC+ Trainer
A dedicated learning experience modeled closely on verified EPSON RC+ concepts and workflows. It should teach the user how RC+ is organized while using the Android simulator as the visual execution environment.

Key characteristics:
- RC+-like terminology and conceptual organization;
- projects/programs/functions/points/I-O/robot-management concepts;
- real SPEL+ source as a first-class view;
- contextual ? help for unfamiliar RC+ screens and concepts;
- improved 3D robot/workcell visualization alongside the RC+-like workflow.

### Visual Lab
A separate enhanced interface designed around the app's own strengths.

Key characteristics:
- direct touch robot/TCP manipulation;
- richer 3D workcell view;
- simplified and visual controls;
- visual <-> SPEL+ synchronized programming;
- tool, actuator, sensor, part, and process simulation;
- guided learning overlays and contextual ? help.

Both experiences should share the same underlying simulation/project state wherever practical.

## User-facing modes

### Explore
Tap J1/J2/... or the tool to highlight it, show its axis, limits and explanation.

### Jog
- Joint mode
- Cartesian XYZ mode
- Tool-coordinate mode
- Local/base mode
- configurable speed
- Home/reset
- limit visualization

### Touch Move
Directly drag the TCP in 3D. IK calculates a valid joint solution while respecting joint limits.

### Touch Target
Tap/drag a desired target pose. Show a ghost solution first, then allow the simulated MOVE.

### Teach Points
- save current pose as P1/P2/...
- rename points;
- edit coordinates;
- visualize points in space;
- display path between points;
- import/export point data later.

### Program
Create and execute a simple Epson-inspired educational program representation, line by line. The simulation layer must not pretend this syntax is valid on a specific real controller unless verified against that controller/software version.

Examples of educational actions:
- MotorOn
- Speed
- Go / Move to point
- Wait
- OpenGripper / CloseGripper
- VacuumOn / VacuumOff
- WeldStart / WeldStop

### Learn
Guided challenges such as:
- move J2 to a specified angle;
- move TCP to an XYZ target;
- explain Joint vs Cartesian motion;
- save a teach point;
- choose the correct tool TCP;
- build a pick-and-place sequence.

## 3D simulation capabilities

- articulated robot model;
- camera orbit/pan/zoom;
- selected-axis visualization;
- world/base/tool coordinate frames;
- TCP marker;
- joint limits;
- reachable workspace;
- ghost pose;
- point markers;
- path visualization;
- collision representation;
- workcell objects;
- target objects that can be grasped.

## Kinematics

Phase order:
1. forward kinematics;
2. joint-limit enforcement;
3. IK for position;
4. IK for pose/orientation;
5. multiple-solution handling;
6. singularity warnings;
7. collision-aware validation;
8. trajectory interpolation.

Kinematics must be data-driven from each robot definition.

## End effectors and process tools

All tools use the same plugin-style contract:
- 3D asset;
- mount transform;
- TCP;
- collision shapes;
- tool state;
- simulation commands;
- optional interaction logic;
- optional process visualization.

First implementations:
1. two-finger gripper;
2. vacuum cup;
3. welding torch;
4. simple articulated hand.

## Workcell simulation

Later phases:
- tables;
- fixtures;
- boxes/parts;
- conveyors;
- pallets;
- welding workpieces;
- snap points;
- graspable rigid objects.

## Data model

RobotDefinition
- id
- displayName
- joint definitions
- kinematic chain
- home pose
- joint limits
- 3D asset mapping
- flange transform
- metadata

ToolDefinition
- id
- displayName
- mount transform
- TCP
- collision geometry
- command capabilities
- visualization asset

Pose
- X/Y/Z
- orientation
- optional joint solution

TeachPoint
- id/name
- pose
- preferred joint solution
- tool/local context

Program
- ordered actions
- simulation execution state
- diagnostics

## Future Epson integration

A later phase can add a Windows-side bridge between the Android app and Epson software/simulator.

Proposed boundary:

Android App <-> authenticated local protocol <-> Windows Epson Bridge <-> Epson environment

This must be a separate module. Direct robot control is explicitly out of scope for the initial simulator.

## Definition of first useful release

The first milestone is complete when a user can:
1. open a 3D Epson robot;
2. rotate the camera;
3. move joints;
4. drag the TCP;
5. obtain valid IK;
6. see current joint and XYZ values;
7. preview a ghost target;
8. save P1/P2;
9. attach a functional gripper;
10. execute a simulated pick-and-place sequence.

## Information still needed

- exact Epson robot model used by the current Windows simulator;
- screenshots/version of the current Epson software;
- joint specifications for that model if not obtainable from public documentation;
- preferred Android phone/tablet form factor.

Until the model is confirmed, robot-specific geometry and kinematic constants remain placeholders.