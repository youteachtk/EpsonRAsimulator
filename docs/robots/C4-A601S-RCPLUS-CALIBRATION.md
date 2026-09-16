# C4-A601S RC+ calibration procedure

Purpose: validate the simulator's positive joint directions and map the CAD coordinate frame to EPSON RC+ X/Y/Z.

## Simulator calibration pose

Use:

| Joint | Value |
| --- | ---: |
| J1 | +20° |
| J2 | -20° |
| J3 | +30° |
| J4 | +25° |
| J5 | +15° |
| J6 | +40° |

The Android app exposes this as **RC+ TEST POSE**.

## RC+ comparison

In EPSON RC+ 7.0 / Robot Manager:

1. Use the simulator, not physical hardware.
2. Set the same six joint values.
3. Capture the Robot Manager view showing:
   - J1–J6/current joint values;
   - X, Y, Z;
   - U, V, W if visible;
   - the robot pose.

## What we validate

- whether each positive joint direction matches the app;
- whether RC+ X/Y axes correspond to the current CAD-to-Z-up mapping;
- flange/TCP zero offset;
- later, U/V/W orientation convention.

## Current provisional mapping

The Android app currently displays a candidate Z-up mapping:

- X = CAD X
- Y = -CAD Z
- Z = CAD Y

These values are intentionally marked **CALIBRATION** until verified against RC+.
