# Touch and Interaction Model

## Design goal

A learner should be able to point at what they want to manipulate instead of translating every intention into numeric controls.

## Camera gestures

When no manipulator is selected:
- one-finger drag: orbit camera;
- two-finger pinch: zoom;
- two-finger drag: pan.

## Direct TCP manipulation

Tap the TCP to select it.

While selected:
- drag on screen-plane handle: move in the visible plane;
- drag X/Y/Z handle: constrain to one axis;
- drag planar handle: constrain to XY/XZ/YZ;
- rotate orientation rings: change TCP orientation.

The app continuously attempts IK and renders a valid candidate.

## Touch Target

Alternative mode:
1. user taps or drags a target;
2. target marker appears;
3. IK finds one or more solutions;
4. preferred solution is rendered as a translucent ghost robot;
5. UI shows reachable / limited / singular / collision state;
6. user applies the simulated move.

## Joint manipulation

Tap a robot joint:
- joint is highlighted;
- its axis is rendered;
- circular/linear drag manipulates only that joint;
- numeric value and limits remain visible.

## Reach feedback

- valid: target accepted;
- joint-limited: target cannot be reached under limits;
- unreachable: no IK solution;
- singular: warning and optional alternative pose;
- collision: invalid target when collision validation is enabled.

Do not silently snap an unreachable target into a misleading valid position.

## Precision controls

Touch is complemented by:
- fine/coarse step mode;
- numeric editing;
- speed slider;
- hold-to-jog buttons;
- reset/home.

## Accessibility

Controls must remain usable on tablets and phones:
- large touch targets;
- readable numeric values;
- landscape-friendly layout;
- no reliance on color alone for state.
