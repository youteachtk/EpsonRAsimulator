# Commercialization and Legal Risk Research

Status: product/legal research only. Not legal advice and not a final monetization decision.

## Commercialization premise

EpsonRAsimulator could support a freemium model, but commercialization should be gated by an IP/licensing review before publication.

### Candidate model A — Freemium subscription
Free:
- Visual Lab basics
- core C4-A601S simulation
- joint/cartesian jog basics
- introductory RC+ Trainer
- limited lessons and projects

Premium:
- Full Learning RC+ profile
- optional RC+ modules
- advanced workcells
- full bilingual guided curriculum
- advanced SPEL+ visual/source tools
- additional robots/tools/processes
- cloud sync / backup / classroom progress features if later implemented

Pros: low adoption barrier and recurring revenue.
Cons: subscription policy, billing, taxes, cancellations, and recurring-value requirements.

### Candidate model B — Free + one-time Pro unlock
Free basics plus a permanent Pro feature pack.

Pros: simpler customer proposition.
Cons: weaker recurring revenue and harder to fund ongoing module/content updates.

### Candidate model C — Individual + Education licensing
Free learner tier, individual premium tier, and school/classroom license.

Pros: fits the educational use case and could support instructors/classes.
Cons: more account, privacy, procurement, and administration complexity.

Current product recommendation for later approval: combine A + C (Free learner, Premium individual subscription, Education/Institution plan), while retaining an optional lifetime/one-time offer only if commercially useful.

## High-priority legal/IP risks

### 1. Epson trademark / brand confusion
EPSON is a registered trademark. A commercial app must not imply that it is an official Epson product or endorsed by Epson unless written authorization exists.

Mitigation:
- do not use Epson logos or official brand artwork without permission;
- use a distinct product brand;
- use Epson / EPSON RC+ references descriptively for compatibility/training only and with a clear non-affiliation notice;
- obtain trademark counsel or written permission before launch if the commercial title prominently includes Epson/EPSON RC+.

### 2. RC+ UI fidelity vs copyrighted expressive assets
Functional/structural fidelity is the learning goal, but copying Epson's source code, icons, screenshots, manuals, original illustrations, exact proprietary graphic assets, or manual text creates copyright/IP risk.

Mitigation:
- reproduce workflows and behavior through independently written code;
- create original icons, visual skin, help text, diagrams and instructional content;
- do not embed scans/screenshots from RC+ as the production UI;
- use manuals as factual/reference material, not as assets to copy;
- maintain a source/provenance record for every third-party asset.

### 3. RC+ software EULA / reverse engineering
The Epson RC+ software license states that users may not modify/adapt/translate/create derivative works from the software or reverse engineer/decompile/disassemble it.

Mitigation:
- do not inspect/disassemble proprietary RC+ binaries or extract proprietary resources;
- derive compatibility from documented public behavior, manuals, user-observed workflows, documented APIs/protocols, and permitted testing;
- keep the Android implementation independently authored.

### 4. Epson CAD/model asset redistribution
The project currently uses GLB geometry derived from an Epson-published STEP model. Commercial redistribution rights for that CAD-derived asset have not been verified.

Current project decision:
- do not contact Epson for permission at this stage;
- create an independently authored replacement robot model as the preferred commercial asset;
- keep the present CAD-derived model as a development/reference fallback because its quality is already acceptable to the user;
- do not treat that fallback as commercially cleared unless its redistribution rights are later verified.

Mitigation:
- independently author mesh topology, geometry details, materials, textures and visual assets;
- use lawful factual references such as measurements, dimensions, observed joint relationships and independently captured photographs where permitted;
- separate render assets from kinematic/robot-definition data so the mesh can be swapped without changing simulation logic;
- keep a license/provenance manifest for all robot/tool/workcell assets;
- perform an IP review of the final independently created model before a paid/public release.

### 5. SPEL+ names, syntax, compatibility and documentation
Using real SPEL+ for interoperability/learning is a core requirement, but documentation text and examples are copyrighted even if language syntax/command names need to be referenced for compatibility.

Mitigation:
- independently write parser/generator/runtime code;
- use only the command names/syntax necessary for compatibility;
- write original explanations and examples;
- avoid copying substantial Epson manual prose/example programs;
- use trademark attribution where appropriate.

### 6. Google Play IP / impersonation
Google Play prohibits third-party IP infringement and apps that misleadingly imply affiliation with another company.

Mitigation:
- distinct app icon/name/developer identity;
- no Epson logo;
- explicit "independent training simulator; not affiliated with or endorsed by Seiko Epson Corporation/Epson America" language where appropriate;
- keep written permissions/licenses ready for Play review if third-party IP is used.

### 7. Google Play billing / subscriptions
Digital subscriptions/features sold inside an Android app distributed through Google Play generally fall under Google Play Billing unless an exception/program applies. Subscription terms, price, billing frequency, renewal/cancellation, and recurring value must be clear.

Mitigation:
- design billing around current Play policy;
- provide clear cancellation/management flows;
- do not call a one-time unlock a subscription;
- keep premium value recurring if charging recurring fees.

### 8. Privacy / student data
If later versions add accounts, cloud sync, classroom dashboards, analytics, assignments, or student progress, privacy obligations become material. Mexico's current Federal Law on Protection of Personal Data Held by Private Parties requires lawful, informed, proportionate treatment and security measures.

Mitigation:
- local/offline-first architecture where practical;
- collect the minimum personal data;
- privacy notice and consent flows before collection;
- security controls and deletion/export mechanisms;
- additional review for minors and school deployments.

### 9. Safety / real robot control liability
A product that connects to real industrial robots carries materially higher safety/product-liability risk than a simulator.

Mitigation:
- keep simulation, RC+ digital-twin bridge and any future real-hardware control as separately gated modes;
- never bypass controller safety, E-stop, interlocks or manufacturer safeguards;
- explicit simulation indicators and warnings;
- formal safety review before any real-motion feature ships commercially.

## Commercialization gate before public paid release

Do not launch a paid/public build until all of the following are resolved:
1. Commercial brand/name selected and trademark-confusion review completed.
2. Epson CAD/3D redistribution status resolved or assets replaced.
3. No proprietary RC+ binary/resource extraction in the codebase.
4. Original UI graphics/help/tutorial content audited.
5. SPEL+ compatibility implementation provenance documented.
6. Privacy policy and data map completed if accounts/cloud exist.
7. Google Play billing/premium architecture compliant.
8. Real-hardware connectivity, if any, receives separate safety/legal review.
9. Qualified IP/software counsel reviews the release in intended launch markets.

## Research sources

- Epson US Terms of Use: https://epson.com/terms-of-use
- EPSON RC+ 7.0 User's Guide / Software License Agreement (official Epson support)
- Epson RC+ 7.0 support page: https://epson.com/Support/Robots/Software/Epson-RC%2B-7-0/s/SPT_R12N793031
- Google Play Intellectual Property policy
- Google Play Impersonation policy
- Google Play Payments / Subscriptions policies
- Mexico Federal Law on Protection of Personal Data Held by Private Parties (DOF 20-03-2025; current law should be checked before launch)

### Functional names and labels vs copyrighted visuals

Current design rule:
- exact short functional labels such as menu names, window/tool names, file extensions and programming command names may be retained where needed for compatibility and authentic training;
- do not treat this as a blanket permission to copy Epson branding or expressive UI assets;
- create our own icons, artwork, visual skin, spacing, window chrome, help text and tutorials;
- use EPSON / EPSON RC+ only as descriptive compatibility/training references, with clear non-affiliation language for a commercial release.

Reasoning baseline:
- U.S. Copyright Office guidance states that names, titles and short phrases are generally not copyrightable, but they can still be protected under trademark law.
- Mexico's IMPI recognizes words/names/designs and overall commercial image as potentially protectable distinctive signs.
- Epson states that EPSON and its logos are registered trademarks and that no trademark license is implied by its website/materials.

This is a risk-reduction design rule, not a legal clearance opinion.
