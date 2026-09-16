#!/usr/bin/env python3
"""
Convert the official Epson C4-A601S STEP assembly into mobile GLB parts.

The Epson source CAD is NOT committed to this repository. The build workflow
downloads the public manufacturer package at build time, converts the standard
C4-A600S assembly, and publishes generated GLBs as a temporary build artifact.

The STEP assembly already exposes C4_BASE and C4_J1..C4_J6 as separate solids.
Joint axes below were derived by finding coaxial cylindrical surfaces shared by
adjacent solids in the manufacturer CAD.

Coordinates:
- STEP/CAD frame is right-handed and Y-up.
- Distances in STEP are millimetres.
- GLB files are exported in metres (glTF convention).
"""

from __future__ import annotations

import argparse
import json
from pathlib import Path

import cadquery as cq
import numpy as np
import trimesh
from OCP.STEPCAFControl import STEPCAFControl_Reader
from OCP.TCollection import TCollection_ExtendedString
from OCP.TDataStd import TDataStd_Name
from OCP.TDF import TDF_LabelSequence
from OCP.TDocStd import TDocStd_Document
from OCP.XCAFDoc import XCAFDoc_DocumentTool, XCAFDoc_ShapeTool


PART_ORDER = [
    "C4_BASE",
    "C4_J1",
    "C4_J2",
    "C4_J3",
    "C4_J4",
    "C4_J5",
    "C4_J6",
]

# A point on each rotation axis in the zero-pose CAD frame, mm.
# For collinear axes, the point is chosen at the intersection with the
# neighbouring orthogonal joint axis to make the hierarchy convenient.
PIVOTS_MM = {
    "C4_J1": np.array([0.0, 0.0, 0.0]),
    "C4_J2": np.array([0.0, 320.0, -100.0]),
    "C4_J3": np.array([0.0, 570.0, -100.0]),
    "C4_J4": np.array([0.0, 570.0, -100.0]),
    "C4_J5": np.array([0.0, 570.0, -350.0]),
    "C4_J6": np.array([0.0, 570.0, -350.0]),
}

AXES = {
    "C4_J1": [0.0, 1.0, 0.0],
    "C4_J2": [1.0, 0.0, 0.0],
    "C4_J3": [1.0, 0.0, 0.0],
    "C4_J4": [0.0, 0.0, 1.0],
    "C4_J5": [1.0, 0.0, 0.0],
    "C4_J6": [0.0, 0.0, 1.0],
}

PARENTS = {
    "C4_J1": "world",
    "C4_J2": "C4_J1",
    "C4_J3": "C4_J2",
    "C4_J4": "C4_J3",
    "C4_J5": "C4_J4",
    "C4_J6": "C4_J5",
}


def _label_name(label) -> str:
    attr = TDataStd_Name()
    if label.FindAttribute(TDataStd_Name.GetID_s(), attr):
        return attr.Get().ToExtString()
    return ""


def load_named_shapes(step_path: Path):
    doc = TDocStd_Document(TCollection_ExtendedString("MDTV-XCAF"))
    reader = STEPCAFControl_Reader()
    reader.SetNameMode(True)

    status = reader.ReadFile(str(step_path))
    if "RetDone" not in str(status):
        raise RuntimeError(f"STEP read failed: {status}")
    if not reader.Transfer(doc):
        raise RuntimeError("STEP transfer failed")

    shape_tool = XCAFDoc_DocumentTool.ShapeTool_s(doc.Main())
    free = TDF_LabelSequence()
    shape_tool.GetFreeShapes(free)
    if free.Length() != 1:
        raise RuntimeError(f"Expected one assembly root, got {free.Length()}")

    root = free.Value(1)
    components = TDF_LabelSequence()
    XCAFDoc_ShapeTool.GetComponents_s(root, components, False)

    named = {}
    for i in range(1, components.Length() + 1):
        label = components.Value(i)
        name = _label_name(label)
        if name in PART_ORDER:
            named[name] = XCAFDoc_ShapeTool.GetShape_s(label)

    missing = [name for name in PART_ORDER if name not in named]
    if missing:
        raise RuntimeError(f"Missing expected CAD components: {missing}")
    return named


def tessellate(shape, linear_tolerance: float, angular_tolerance: float):
    vertices, faces = cq.Shape.cast(shape).tessellate(
        linear_tolerance,
        angular_tolerance,
    )
    verts = np.asarray([[v.x, v.y, v.z] for v in vertices], dtype=np.float32)
    tris = np.asarray(faces, dtype=np.int64)
    return verts, tris


def export_part(
    name: str,
    shape,
    output_dir: Path,
    linear_tolerance: float,
    angular_tolerance: float,
):
    vertices, faces = tessellate(shape, linear_tolerance, angular_tolerance)

    if name in PIVOTS_MM:
        vertices = vertices - PIVOTS_MM[name]

    # STEP mm -> glTF metres.
    vertices *= 0.001

    mesh = trimesh.Trimesh(vertices=vertices, faces=faces, process=False)
    mesh.remove_unreferenced_vertices()

    scene = trimesh.Scene()
    scene.add_geometry(mesh, node_name=name, geom_name=name)

    target = output_dir / f"{name}.glb"
    target.write_bytes(scene.export(file_type="glb"))

    return {
        "name": name,
        "vertices": int(len(mesh.vertices)),
        "faces": int(len(mesh.faces)),
        "file": target.name,
        "sizeBytes": target.stat().st_size,
    }


def metadata():
    result = {
        "robot": "Epson C4-A601S",
        "sourceAssembly": "C4-A600S_ASM",
        "unit": "metre",
        "cadUnit": "millimetre",
        "coordinateFrame": "CAD Y-up",
        "angleSignStatus": "pending RC+ positive-direction validation",
        "joints": [],
    }

    for index in range(1, 7):
        name = f"C4_J{index}"
        parent = PARENTS[name]
        parent_pivot = (
            np.zeros(3)
            if parent == "world"
            else PIVOTS_MM[parent]
        )
        relative_mm = PIVOTS_MM[name] - parent_pivot
        result["joints"].append(
            {
                "id": f"J{index}",
                "node": name,
                "parent": parent,
                "axis": AXES[name],
                "pivotCadMm": PIVOTS_MM[name].tolist(),
                "relativePositionM": (relative_mm * 0.001).tolist(),
                "visualAngleSign": 1.0,
            }
        )
    return result


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("step", type=Path)
    parser.add_argument("output", type=Path)
    parser.add_argument("--linear-tolerance", type=float, default=1.5)
    parser.add_argument("--angular-tolerance", type=float, default=0.3)
    args = parser.parse_args()

    args.output.mkdir(parents=True, exist_ok=True)
    shapes = load_named_shapes(args.step)

    stats = []
    for name in PART_ORDER:
        stats.append(
            export_part(
                name,
                shapes[name],
                args.output,
                args.linear_tolerance,
                args.angular_tolerance,
            )
        )

    info = metadata()
    info["assets"] = stats
    (args.output / "kinematics.json").write_text(
        json.dumps(info, indent=2),
        encoding="utf-8",
    )

    print(json.dumps(info, indent=2))


if __name__ == "__main__":
    main()
