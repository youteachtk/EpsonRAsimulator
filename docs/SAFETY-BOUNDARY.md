# Safety Boundary

## Initial scope

EpsonRAsimulator is a learning and simulation application.

The initial app:
- does not command physical motors;
- does not claim simulated paths are safe for a real cell;
- does not bypass a robot controller's safety system;
- does not replace manufacturer training, guarding, risk assessment or emergency-stop systems.

## Future bridge

Any future integration must distinguish at least:
- SIMULATION;
- CONNECTED_READ_ONLY;
- EXTERNAL_SIMULATOR_CONTROL;
- REAL_HARDWARE_CONTROL.

Real hardware control is not part of the initial roadmap milestone and requires a separate technical/safety specification.

## UI rule

A user must never be able to confuse simulated motion with real-hardware-enabled motion. Connection state must be persistent, explicit and visually unmistakable.

## Engineering rule

Simulation code must not depend on real-controller transport code. The external bridge is an adapter around validated domain commands, never the owner of kinematic truth or UI state.
