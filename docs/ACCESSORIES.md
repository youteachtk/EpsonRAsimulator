# Accessories and End Effectors

Accessories are first-class simulation components.

## Tool contract

Every tool can define:

- ID and display name;
- GLB/glTF visual asset;
- mounting transform relative to robot flange;
- one or more TCP frames;
- collision geometry;
- state;
- supported simulated commands;
- grasp/process behavior;
- teaching notes.

## Two-finger gripper

States:
- open;
- closing;
- closed;
- holding;
- blocked.

Simulation:
- finger animation;
- object detection in grasp volume;
- attach eligible object when grasp succeeds;
- carry object with TCP;
- release object when opened.

Commands:
- OpenGripper
- CloseGripper

## Vacuum gripper

States:
- vacuum off;
- vacuum on;
- attached;
- seal failed.

Commands:
- VacuumOn
- VacuumOff

Simulation can require the cup to be near a compatible surface and properly oriented.

## Robotic hand

Initial educational implementation:
- open hand;
- close hand;
- pinch grasp;
- cylindrical grasp.

A future detailed hand can have finger joints, but it must still expose a simple high-level command contract.

## Welding torch

Defines:
- torch TCP;
- approach axis;
- standoff visualization;
- simulated process state;
- path/cord visualization.

Commands:
- WeldStart
- WeldStop

The simulator can display path quality hints such as orientation/standoff. It does not claim that simulated welding settings are production-safe.

## Custom tool

The long-term editor should let the user:
1. import/select a 3D tool;
2. align its mount;
3. specify TCP position/orientation;
4. define basic collision bounds;
5. choose capabilities;
6. save it in the user's tool library.

## Tool change behavior

Attaching a new tool must update:
- rendered geometry;
- active TCP;
- coordinate frame;
- reach calculations using the TCP;
- collision model;
- available commands;
- program validation.

## Planned accessory order

1. two-finger gripper;
2. vacuum;
3. welding torch;
4. hand;
5. custom-tool editor;
6. sensor/process extensions.
