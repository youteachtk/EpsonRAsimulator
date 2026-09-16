# C4-A601S CAD-derived kinematic structure

## Source

The conversion pipeline downloads Epson's official C4-A601S STEP package at build time. The vendor CAD itself is intentionally not committed to this public repository.

The standard package contains two assemblies:

- `c4-a600s_asm.stp`
- `c4-a600sr_asm.stp`

For the configured **C4-A601S**, the simulator baseline uses the standard `C4-A600S_ASM` geometry.

## Important discovery

The manufacturer STEP already separates the robot into these solids:

- `C4_BASE`
- `C4_J1`
- `C4_J2`
- `C4_J3`
- `C4_J4`
- `C4_J5`
- `C4_J6`

Therefore no manual mesh cutting is required.

## Joint axes derived from CAD

The axes were derived by detecting coaxial cylindrical surfaces shared by adjacent solids. Coordinates are in the manufacturer STEP frame (millimetres, Y-up).

| Joint | CAD axis | Point on axis (mm) | Evidence |
| --- | --- | --- | --- |
| J1 | +Y | (0, 0, 0) | BASE/J1 share coaxial Y cylinders on x=0, z=0 |
| J2 | +X | (0, 320, -100) | J1/J2 share coaxial X cylinders |
| J3 | +X | (0, 570, -100) | J2/J3 share coaxial X cylinders |
| J4 | +Z | (0, 570, -100) | J3/J4 share coaxial Z cylinders on x=0, y=570 |
| J5 | +X | (0, 570, -350) | J4/J5 share coaxial X cylinders |
| J6 | +Z | (0, 570, -350) | J5/J6 share coaxial Z cylinders on x=0, y=570 |

J4 and J6 are collinear in the zero pose when J5 = 0, as expected for this wrist geometry.

## Hierarchy used by the app

```
world
├── C4_BASE
└── J1 (+Y)
    └── J2 (+X), offset (0, 0.320, -0.100) m
        └── J3 (+X), offset (0, 0.250, 0) m
            └── J4 (+Z), same origin as J3
                └── J5 (+X), offset (0, 0, -0.250) m
                    └── J6 (+Z), same origin as J5
```

Each moving GLB is exported in **local joint coordinates**, with its vertices translated so its rotation axis passes through that node's origin. This allows Jetpack Compose / SceneView to rotate each joint as a normal nested node.

## Calibration still required

The CAD establishes the physical axes and origins. The **positive angular direction used by EPSON RC+** for J1–J6 will be validated separately before the simulator claims exact RC+ angle parity. Until that test is completed, visual angle signs remain provisional.

## Mobile tessellation

The default converter uses:

- linear tessellation tolerance: 1.5 mm
- angular tolerance: 0.3 rad
- output units: metres (glTF convention)

The current combined geometry is approximately 5 MB before app packaging, while preserving seven independently movable parts.
