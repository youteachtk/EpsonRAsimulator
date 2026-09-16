# Competitive Landscape — September 2026

## Question

Does a product already exist that combines the same goals as this project?

Current project combination:
- native/mobile-first training experience;
- high-fidelity training of a vendor development environment (initially EPSON RC+ 7.0 v7.5.3);
- functional 3D robot/workcell simulation;
- real vendor programming language (initially SPEL+);
- visual/touch-first alternative interface over the same runtime;
- shared project/points/I-O/task/workcell state;
- contextual bilingual learning/help;
- future bridge to the real vendor desktop environment;
- extensibility to additional robots and simulator ecosystems.

## Finding

As of 2026-09-16, there are several products that overlap strongly with parts of this concept, but no public product was found that clearly combines the entire set above in the same way.

This is not proof that no such product exists. Private/internal training systems, regional products, research prototypes, or unindexed commercial tools may exist.

## Closest products / categories

### Epson RC+ 7.0 / RC+ 8.0

Epson's own RC+ already provides:
- real SPEL+ programming;
- project management;
- Robot Manager and integrated robot tools;
- 3D robot simulation;
- program execution in simulation;
- I/O and option integration;
- official training material/courses.

RC+ 8.0 is the current generation and includes comprehensive 3D simulation/programming tools and virtual-controller workflows.

Implication:
The project must not position basic 3D simulation or SPEL+ programming alone as unique. The educational/mobile/dual-interface layer is more differentiated.

Sources:
- https://epson.com/For-Work/Robots/Software/Epson-RC%2B-8-0-Software/p/RRCPLUS80SW
- https://epson.com/Support/Robots/Software/Epson-RC%2B-7-0/s/SPT_R12N793031
- https://epson.com/robotics-for-education

### Epson RC+ Express

This is the closest Epson product to the Visual Lab idea.

It provides:
- visual/block-style robot teaching;
- templates for pick-and-place and palletizing;
- 3D simulation;
- simplified jogging, gripper control and motion;
- advanced SPEL+ capability / project-link workflow;
- touch support on Windows tablets.

Important difference:
Epson describes RC+ Express as a Windows tablet/PC environment, not an Android-native RC+ fidelity trainer. It is designed to simplify robot application creation rather than reproduce the complete RC+ desktop environment for training.

Source:
- https://epson.com/For-Work/Robots/Integrated-Options/Epson-RC%2B-Express/p/RRCPLUSEXPSW

### RoboDK

RoboDK overlaps strongly with the multi-robot / Visual Lab / digital-twin direction:
- industrial robot simulation and offline programming;
- large multi-brand robot library, including Epson;
- digital twins;
- collision/singularity/reachability handling;
- external axes;
- robot program generation through post-processors;
- education licensing and training resources;
- historical Android/iOS support and current Web access usable from mobile devices.

Important difference:
RoboDK intentionally provides a vendor-agnostic programming/simulation workflow. Its value proposition is not to reproduce each manufacturer's full native IDE as a fidelity trainer. The current public download experience emphasizes Windows/macOS/Linux plus RoboDK for Web; a 2026 forum response points Android users toward RoboDK Web while a native Android update has no published date.

Sources:
- https://robodk.com/
- https://robodk.com/about
- https://robodk.com/download
- https://robodk.com/pricing

### ABB RobotStudio / RobotStudio Cloud / AR Viewer

ABB demonstrates that several parts of the proposed strategy are commercially validated:
- desktop high-fidelity simulator using a virtual controller;
- real robot programs/configuration;
- education use;
- browser-based programming/simulation through RobotStudio Cloud;
- access "from any device";
- mobile AR/3D viewing and joint jogging.

Important difference:
It is an ABB ecosystem rather than a multi-simulator training shell, and its mobile/cloud products do not appear to be designed as replicas of third-party robot IDEs.

Sources:
- https://www.abb.com/global/en/areas/robotics/products/software/robotstudio-suite
- https://www.abb.com/global/en/areas/robotics/products/software/robotstudio-suite/robotstudio-cloud
- https://www.abb.com/global/en/areas/robotics/products/software/robotstudio-suite/robotstudio-ar-viewer

### Visual Components OLP

Strong overlap with the future multi-brand workcell direction:
- multi-brand robot programming;
- 22 robot brands / 40+ controllers in OLP post-processors;
- 1,900+ robots from 60+ brands in its eCatalog;
- cell/factory simulation;
- workcell equipment and process simulation;
- offline program generation;
- virtual commissioning.

Important difference:
Its goal is vendor-agnostic manufacturing engineering and OLP, not high-fidelity emulation/training of each manufacturer's native programming environment.

Source:
- https://www.visualcomponents.com/products/robot-offline-programming/

### FANUC ROBOGUIDE

Overlaps with:
- accurate manufacturer-specific simulation;
- real controller/programming model;
- 3D workcell simulation;
- virtual teach-pendant workflows;
- training use.

It is PC software focused on FANUC rather than an Android multi-environment trainer.

Source:
- https://www.fanucamerica.com/products/software/robot/roboguide

### KUKA.Sim

Overlaps with:
- robot/workcell simulation;
- CAD;
- conveyors/physics;
- welding and other process add-ons;
- offline programming/virtual commissioning.

It is a KUKA engineering/simulation environment rather than a mobile multi-vendor fidelity trainer.

Source:
- https://www.kuka.com/en-us/products/robotics-systems/software/simulation-planning-optimization/kuka_sim/kuka_sim_updates

### Universal Robots URSim / UR Academy

UR provides:
- a free offline simulator that reproduces PolyScope robot programming;
- programs that can later be transferred to a robot;
- browser-based Academy learning modules using simulated training workflows.

This validates the "learn the real interface without hardware" concept, but it is vendor-specific and does not provide the planned RC+ Trainer + Visual Lab shared-runtime architecture.

Sources:
- https://www.universal-robots.com/download/software-ur-series/simulator-linux/offline-simulator-ur-series-e-series-ur-sim-for-linux-5252/
- https://academy.universal-robots.com/es/formacion-en-linea-gratuita/formacion-en-linea-de-e-series/itinerario-basico-de-e-series/

### Multi-brand industrial training simulators

Some specialized training products provide switchable virtual industrial robot environments and simulated/physical teach pendants. These validate demand for multi-brand operator training, but they are generally dedicated lab hardware/PC systems rather than Android-native shared-runtime products.

Example:
- https://www.yalongeducation.com/robotics/industrial-robot-simulator.html

## Competitive conclusion

The market already validates nearly every individual building block:
- accurate manufacturer simulation;
- vendor-native programming;
- visual/block programming;
- mobile/web robot simulation;
- multi-brand robot libraries;
- digital twins/workcells;
- educational simulation;
- virtual teach-pendant/IDE training.

The more defensible product combination is therefore not "a robot simulator" by itself.

The differentiated target is the combination of:
1. high-fidelity learning of real vendor software/workflows;
2. a friendlier Visual Lab over the same canonical runtime;
3. native/touch-first mobile use, preferably with useful offline capability;
4. real vendor-language preservation rather than only generic post-processing;
5. bilingual contextual teaching built into the interface;
6. modular expansion to multiple vendor simulators while preserving each vendor's native mental model;
7. project/point/I-O/workcell state shared across trainer and visual modes.

## Product implications

- Keep EPSON RC+ 7.0 v7.5.3 as the School Setup baseline because it matches the actual learning environment.
- Add RC+ 8.x to the future simulator-profile roadmap because it is Epson's current generation.
- Treat RC+ Express as an important competitive reference for beginner-friendly visual interaction, but do not copy its UI/assets.
- Treat RoboDK and Visual Components as important references for multi-robot architecture and catalog scalability, while preserving this project's different educational-fidelity goal.
- Treat RobotStudio/ROBOGUIDE/URSim as evidence that high-fidelity virtual-controller/interface training is valuable.
- Validate differentiation continuously as the product approaches commercialization.
