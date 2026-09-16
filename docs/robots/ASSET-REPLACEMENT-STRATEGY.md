# Robot Asset Replacement Strategy

Status: approved product/asset direction.

## Goal

Replace the current CAD-derived C4-A601S render assets with an independently authored visual model suitable for long-term commercialization, while preserving the simulator's existing kinematics and interaction behavior.

## Non-negotiable architecture

Robot logic must not depend on the detailed render mesh.

Keep separate:
- RobotDefinition
- joint hierarchy
- J1-J6 pivot positions
- joint axes and signs
- joint limits
- flange transform
- TCP/tool transforms
- collision proxies
- render meshes/materials/textures

This allows the current reference model and the future independent model to use the same robot logic.

## Preferred reconstruction inputs

Use, where lawful and practical:
- our own photographs of the physical school robot;
- our own measurements of visible dimensions;
- manufacturer-published dimensional/specification facts;
- observed joint locations and ranges;
- our already validated kinematic skeleton as a technical reference;
- independently authored materials, decals and surface details.

Do not copy proprietary mesh topology, textures, icons, logos or hidden CAD details into the replacement.

## Modeling workflow

1. Lock the validated kinematic skeleton.
2. Capture reference photos from front, rear, both sides, top/oblique views.
3. Record key physical measurements.
4. Build low/medium-poly link shells independently around J1-J6 pivots.
5. Match silhouette and proportions.
6. Create our own material set and markings.
7. Export each articulated link independently.
8. Bind meshes to the existing hierarchy.
9. Compare side-by-side against the real robot and current reference model.
10. Validate Android performance, joint motion, flange alignment and collision proxies.

## Quality bar

The replacement is acceptable only if it:
- is immediately recognizable as the target C4-class robot;
- looks at least as polished as the current in-app model;
- preserves correct articulation;
- keeps the flange/TCP location aligned;
- performs well on the target Android devices.

## Fallback

If the independently authored replacement is not yet visually acceptable, continue using the current model for private development/testing while improving the replacement. The current CAD-derived model remains a legal/commercial review blocker for public paid redistribution until rights are verified.
