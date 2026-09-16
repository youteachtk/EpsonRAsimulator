# Shared Runtime Foundation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move the existing C4 simulator onto a neutral shared runtime with robot/provider and simulator/language/project-format adapter registries, while preserving the current visible C4 behavior.

**Architecture:** Keep the current single Android app module, but introduce pure-Kotlin neutral contracts under focused packages. The shared runtime becomes authoritative for robot selection, joint state, teach points, connection mode and selected simulator/profile; the Compose UI observes that runtime instead of owning independent joint state. RC+ 7.0 v7.5.3 and SPEL+ are registered as the first adapters, but the core runtime does not depend on RC+-specific UI.

**Tech Stack:** Kotlin, Android, Jetpack Compose, Material 3, SceneView/Filament 4.35.0, JUnit 4.13.2, Gradle 9.6.0, Java 17.

**Spec:** `docs/superpowers/specs/2026-09-16-rcplus-trainer-shared-runtime-design.md`

## Global Constraints

- Android namespace/application id remains `mx.youteachtk.epsonrasimulator`.
- `compileSdk = 37`, `targetSdk = 37`, `minSdk = 24`.
- Java source/target compatibility remains 17.
- SceneView remains pinned at `io.github.sceneview:sceneview:4.35.0`.
- Existing C4-A601S kinematics and render assets remain behaviorally unchanged in this phase.
- EPSON RC+ 7.0 v7.5.3 + C4-A601S remains the School Setup reference baseline.
- Shared runtime code must not import Compose, SceneView, or RC+-specific UI.
- RC+ 7/SPEL+/project-format behavior enters the application through adapter contracts.
- Local Simulation remains the only executable authority mode in this phase; no Windows bridge or physical-robot control is added.
- Native-source parsing, task scheduling, digital I/O runtime, functional workcell physics, RC+ MDI windows, cloud sync, and Windows bridge are outside this plan and belong to later plans in `2026-09-16-implementation-sequence.md`.
- Use TDD for pure Kotlin behavior: failing test first, verify failure, minimal implementation, verify pass.
- Run the repository's CI-equivalent commands before completion: `gradle testDebugUnitTest --stacktrace` and `gradle assembleDebug --stacktrace`.
- Production changes are executed on an isolated feature branch/worktree created at execution time.

---

## File Structure Map

New neutral runtime/provider code:

```text
app/src/main/java/mx/youteachtk/epsonrasimulator/
├── robot/
│   ├── RobotProvider.kt
│   ├── RobotRegistry.kt
│   └── EpsonRobotProvider.kt
├── adapters/
│   ├── AdapterIds.kt
│   ├── ProgrammingLanguageAdapter.kt
│   ├── ProjectFormatAdapter.kt
│   ├── SimulatorAdapter.kt
│   ├── AdapterRegistry.kt
│   └── rcplus/
│       ├── RcPlusCapabilities.kt
│       ├── SpelPlusLanguageAdapter.kt
│       ├── RcPlusProjectFormatAdapter.kt
│       └── RcPlus7SimulatorAdapter.kt
└── runtime/
    ├── CapabilityModels.kt
    ├── ConnectionMode.kt
    ├── SharedRuntimeState.kt
    ├── RuntimeCommand.kt
    ├── SharedRuntime.kt
    └── AppRuntimeFactory.kt
```

UI integration:

```text
app/src/main/java/mx/youteachtk/epsonrasimulator/
├── MainActivity.kt
└── ui/
    ├── RuntimeStateBinding.kt
    └── RobotTrainerScreen.kt
```

New tests mirror the pure-Kotlin packages:

```text
app/src/test/java/mx/youteachtk/epsonrasimulator/
├── robot/RobotRegistryTest.kt
├── adapters/AdapterRegistryTest.kt
└── runtime/
    ├── CapabilityModelsTest.kt
    ├── SharedRuntimeTest.kt
    └── AppRuntimeFactoryTest.kt
```

Existing files intentionally retained:
- `domain/RobotModels.kt`
- `domain/EpsonRobotCatalog.kt`
- `kinematics/C4Kinematics.kt`
- `ui/C4RobotScene.kt`

The existing Epson catalog remains the factual C4 definition source in this phase; `EpsonRobotProvider` wraps it so UI/runtime code stops depending on that catalog directly.

---

### Task 1: Add RobotProvider and RobotRegistry

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/robot/RobotProvider.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/robot/RobotRegistry.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/robot/EpsonRobotProvider.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/robot/RobotRegistryTest.kt`

**Interfaces:**
- Consumes: existing `domain.RobotDefinition` and `domain.EpsonRobotCatalog.C4_A601S`.
- Produces:
  - `interface RobotProvider { val providerId: String; val robots: List<RobotDefinition> }`
  - `class RobotRegistry(providers: List<RobotProvider>)`
  - `fun RobotRegistry.find(robotId: String): RobotDefinition?`
  - `fun RobotRegistry.require(robotId: String): RobotDefinition`
  - `object EpsonRobotProvider : RobotProvider`

- [ ] **Step 1: Write the failing registry tests**

Create `RobotRegistryTest.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.JointDefinition
import mx.youteachtk.epsonrasimulator.domain.JointType
import mx.youteachtk.epsonrasimulator.domain.RobotDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RobotRegistryTest {
    private fun robot(id: String) = RobotDefinition(
        id = id,
        displayName = id,
        joints = listOf(
            JointDefinition(
                id = "J1",
                displayName = "J1",
                type = JointType.REVOLUTE,
                minValue = -180.0,
                maxValue = 180.0
            )
        )
    )

    @Test
    fun findsRobotAcrossProviders() {
        val provider = object : RobotProvider {
            override val providerId = "fake"
            override val robots = listOf(robot("robot-a"))
        }

        val registry = RobotRegistry(listOf(provider))

        assertEquals("robot-a", registry.require("robot-a").id)
        assertNull(registry.find("missing"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsDuplicateRobotIdsAcrossProviders() {
        val first = object : RobotProvider {
            override val providerId = "first"
            override val robots = listOf(robot("duplicate"))
        }
        val second = object : RobotProvider {
            override val providerId = "second"
            override val robots = listOf(robot("duplicate"))
        }

        RobotRegistry(listOf(first, second))
    }
}
```

- [ ] **Step 2: Run the focused test and verify it fails**

Run:

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.robot.RobotRegistryTest" --stacktrace
```

Expected: FAIL because `RobotProvider` and `RobotRegistry` do not exist.

- [ ] **Step 3: Implement RobotProvider**

Create `RobotProvider.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.RobotDefinition

interface RobotProvider {
    val providerId: String
    val robots: List<RobotDefinition>
}
```

- [ ] **Step 4: Implement RobotRegistry**

Create `RobotRegistry.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.RobotDefinition

class RobotRegistry(providers: List<RobotProvider>) {
    private val robotsById: Map<String, RobotDefinition>

    init {
        val definitions = providers.flatMap { it.robots }
        val duplicates = definitions
            .groupBy { it.id }
            .filterValues { it.size > 1 }
            .keys

        require(duplicates.isEmpty()) {
            "Duplicate robot ids: ${duplicates.sorted().joinToString()}"
        }

        robotsById = definitions.associateBy { it.id }
    }

    fun find(robotId: String): RobotDefinition? = robotsById[robotId]

    fun require(robotId: String): RobotDefinition =
        requireNotNull(find(robotId)) { "Unknown robot id: $robotId" }
}
```

- [ ] **Step 5: Wrap the current Epson catalog in a provider**

Create `EpsonRobotProvider.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.robot

import mx.youteachtk.epsonrasimulator.domain.EpsonRobotCatalog
import mx.youteachtk.epsonrasimulator.domain.RobotDefinition

object EpsonRobotProvider : RobotProvider {
    override val providerId: String = "epson"

    override val robots: List<RobotDefinition> = listOf(
        EpsonRobotCatalog.C4_A601S
    )
}
```

- [ ] **Step 6: Re-run the focused test**

Run:

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.robot.RobotRegistryTest" --stacktrace
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/robot app/src/test/java/mx/youteachtk/epsonrasimulator/robot
git commit -m "feat: add neutral robot provider registry"
```

---

### Task 2: Add neutral capability, profile, and connection-mode models

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/CapabilityModels.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/ConnectionMode.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/CapabilityModelsTest.kt`

**Interfaces:**
- Consumes: no vendor-specific types.
- Produces:
  - `CapabilityId`
  - `CapabilitySet`
  - `TrainingProfileId`
  - `ConnectionMode.LOCAL_SIMULATION`
  - reserved enum values `RCPLUS_DIGITAL_TWIN` and `REAL_HARDWARE` for state representation only; this plan only permits Local Simulation execution.

- [ ] **Step 1: Write failing capability tests**

Create `CapabilityModelsTest.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CapabilityModelsTest {
    @Test
    fun capabilitySetChecksSingleAndMultipleRequirements() {
        val robotManager = CapabilityId("robot-manager")
        val ioMonitor = CapabilityId("io-monitor")
        val set = CapabilitySet(setOf(robotManager, ioMonitor))

        assertTrue(robotManager in set)
        assertTrue(set.containsAll(setOf(robotManager, ioMonitor)))
        assertFalse(set.containsAll(setOf(CapabilityId("vision"))))
    }
}
```

- [ ] **Step 2: Run the focused test and verify failure**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.runtime.CapabilityModelsTest" --stacktrace
```

Expected: FAIL because the runtime capability types do not exist.

- [ ] **Step 3: Implement capability/profile models**

Create `CapabilityModels.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

@JvmInline
value class CapabilityId(val value: String)

data class CapabilitySet(
    val values: Set<CapabilityId> = emptySet()
) {
    operator fun contains(id: CapabilityId): Boolean = id in values

    fun containsAll(required: Set<CapabilityId>): Boolean =
        values.containsAll(required)
}

@JvmInline
value class TrainingProfileId(val value: String)
```

- [ ] **Step 4: Add connection-mode model**

Create `ConnectionMode.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

enum class ConnectionMode {
    LOCAL_SIMULATION,
    RCPLUS_DIGITAL_TWIN,
    REAL_HARDWARE
}
```

Do not add bridge/hardware behavior. The extra enum values only let future state represent the approved architecture without changing the type later.

- [ ] **Step 5: Re-run the test**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.runtime.CapabilityModelsTest" --stacktrace
```

Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/runtime app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/CapabilityModelsTest.kt
git commit -m "feat: add runtime capability and connection models"
```

---

### Task 3: Add simulator, programming-language, and project-format adapter contracts

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/AdapterIds.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/ProgrammingLanguageAdapter.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/ProjectFormatAdapter.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/SimulatorAdapter.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/AdapterRegistry.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/AdapterRegistryTest.kt`

**Interfaces:**
- Consumes: `TrainingProfileId`, `CapabilitySet`.
- Produces:
  - `SimulatorAdapterId`
  - `ProgrammingLanguageAdapterId`
  - `ProjectFormatAdapterId`
  - three adapter interfaces
  - `AdapterRegistry` with validated references.

- [ ] **Step 1: Write failing adapter-registry tests**

Create `AdapterRegistryTest.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters

import mx.youteachtk.epsonrasimulator.runtime.CapabilitySet
import mx.youteachtk.epsonrasimulator.runtime.TrainingProfileId
import org.junit.Assert.assertEquals
import org.junit.Test

class AdapterRegistryTest {
    private val language = object : ProgrammingLanguageAdapter {
        override val id = ProgrammingLanguageAdapterId("lang")
        override val displayName = "Language"
    }

    private val format = object : ProjectFormatAdapter {
        override val id = ProjectFormatAdapterId("format")
        override val displayName = "Format"
        override val fileExtensions = setOf("prg")
    }

    @Test
    fun resolvesSimulatorAndItsReferencedAdapters() {
        val simulator = object : SimulatorAdapter {
            override val id = SimulatorAdapterId("sim")
            override val displayName = "Simulator"
            override val programmingLanguageId = language.id
            override val projectFormatId = format.id
            override val defaultProfileId = TrainingProfileId("school")
            override val capabilities = CapabilitySet()
        }

        val registry = AdapterRegistry(
            simulators = listOf(simulator),
            languages = listOf(language),
            projectFormats = listOf(format)
        )

        assertEquals(language.id, registry.languageFor(simulator.id).id)
        assertEquals(format.id, registry.projectFormatFor(simulator.id).id)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsSimulatorThatReferencesUnknownLanguage() {
        val simulator = object : SimulatorAdapter {
            override val id = SimulatorAdapterId("sim")
            override val displayName = "Simulator"
            override val programmingLanguageId = ProgrammingLanguageAdapterId("missing")
            override val projectFormatId = format.id
            override val defaultProfileId = TrainingProfileId("school")
            override val capabilities = CapabilitySet()
        }

        AdapterRegistry(
            simulators = listOf(simulator),
            languages = emptyList(),
            projectFormats = listOf(format)
        )
    }
}
```

- [ ] **Step 2: Run the focused test and verify failure**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.adapters.AdapterRegistryTest" --stacktrace
```

Expected: FAIL because adapter contracts do not exist.

- [ ] **Step 3: Implement adapter IDs**

Create `AdapterIds.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters

@JvmInline
value class SimulatorAdapterId(val value: String)

@JvmInline
value class ProgrammingLanguageAdapterId(val value: String)

@JvmInline
value class ProjectFormatAdapterId(val value: String)
```

- [ ] **Step 4: Implement adapter interfaces**

Create `ProgrammingLanguageAdapter.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters

interface ProgrammingLanguageAdapter {
    val id: ProgrammingLanguageAdapterId
    val displayName: String
}
```

Create `ProjectFormatAdapter.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters

interface ProjectFormatAdapter {
    val id: ProjectFormatAdapterId
    val displayName: String
    val fileExtensions: Set<String>
}
```

Create `SimulatorAdapter.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters

import mx.youteachtk.epsonrasimulator.runtime.CapabilitySet
import mx.youteachtk.epsonrasimulator.runtime.TrainingProfileId

interface SimulatorAdapter {
    val id: SimulatorAdapterId
    val displayName: String
    val programmingLanguageId: ProgrammingLanguageAdapterId
    val projectFormatId: ProjectFormatAdapterId
    val defaultProfileId: TrainingProfileId
    val capabilities: CapabilitySet
}
```

- [ ] **Step 5: Implement AdapterRegistry with duplicate/reference validation**

Create `AdapterRegistry.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters

class AdapterRegistry(
    simulators: List<SimulatorAdapter>,
    languages: List<ProgrammingLanguageAdapter>,
    projectFormats: List<ProjectFormatAdapter>
) {
    private val simulatorsById = simulators.uniqueById("simulator") { it.id }
    private val languagesById = languages.uniqueById("language") { it.id }
    private val formatsById = projectFormats.uniqueById("project format") { it.id }

    init {
        simulators.forEach { simulator ->
            require(languagesById.containsKey(simulator.programmingLanguageId)) {
                "Simulator ${simulator.id.value} references unknown language ${simulator.programmingLanguageId.value}"
            }
            require(formatsById.containsKey(simulator.projectFormatId)) {
                "Simulator ${simulator.id.value} references unknown project format ${simulator.projectFormatId.value}"
            }
        }
    }

    fun requireSimulator(id: SimulatorAdapterId): SimulatorAdapter =
        requireNotNull(simulatorsById[id]) { "Unknown simulator adapter: ${id.value}" }

    fun languageFor(id: SimulatorAdapterId): ProgrammingLanguageAdapter {
        val simulator = requireSimulator(id)
        return requireNotNull(languagesById[simulator.programmingLanguageId])
    }

    fun projectFormatFor(id: SimulatorAdapterId): ProjectFormatAdapter {
        val simulator = requireSimulator(id)
        return requireNotNull(formatsById[simulator.projectFormatId])
    }

    private fun <T, K> List<T>.uniqueById(
        kind: String,
        id: (T) -> K
    ): Map<K, T> {
        val groups = groupBy(id)
        val duplicates = groups.filterValues { it.size > 1 }.keys
        require(duplicates.isEmpty()) {
            "Duplicate $kind ids: $duplicates"
        }
        return associateBy(id)
    }
}
```

- [ ] **Step 6: Re-run the adapter tests**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.adapters.AdapterRegistryTest" --stacktrace
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/adapters app/src/test/java/mx/youteachtk/epsonrasimulator/adapters
git commit -m "feat: add simulator adapter contracts"
```

---

### Task 4: Register the verified RC+ 7.5.3 / SPEL+ baseline through adapters

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/RcPlusCapabilities.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/SpelPlusLanguageAdapter.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/RcPlusProjectFormatAdapter.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus/RcPlus7SimulatorAdapter.kt`
- Test: extend `app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/AdapterRegistryTest.kt`

**Interfaces:**
- Consumes: adapter contracts and neutral capability/profile types.
- Produces verified baseline adapter IDs:
  - simulator: `epson-rcplus-7.5.3`
  - language: `epson-spel-plus`
  - project format: `epson-rcplus-7-project`
  - default profile: `school-setup`

- [ ] **Step 1: Add a failing baseline-registration test**

Append to `AdapterRegistryTest.kt`:

```kotlin
@Test
fun rcPlus7BaselineResolvesSpelAndProjectFormat() {
    val registry = AdapterRegistry(
        simulators = listOf(mx.youteachtk.epsonrasimulator.adapters.rcplus.RcPlus7SimulatorAdapter),
        languages = listOf(mx.youteachtk.epsonrasimulator.adapters.rcplus.SpelPlusLanguageAdapter),
        projectFormats = listOf(mx.youteachtk.epsonrasimulator.adapters.rcplus.RcPlusProjectFormatAdapter)
    )

    val simulator = registry.requireSimulator(
        SimulatorAdapterId("epson-rcplus-7.5.3")
    )

    assertEquals("epson-spel-plus", registry.languageFor(simulator.id).id.value)
    assertEquals(
        setOf("sprj", "prg", "inc", "pts", "mac"),
        registry.projectFormatFor(simulator.id).fileExtensions
    )
}
```

- [ ] **Step 2: Run the focused test and verify failure**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.adapters.AdapterRegistryTest.rcPlus7BaselineResolvesSpelAndProjectFormat" --stacktrace
```

Expected: FAIL because the RC+ adapter objects do not exist.

- [ ] **Step 3: Define verified core capability IDs**

Create `RcPlusCapabilities.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.runtime.CapabilityId

object RcPlusCapabilities {
    val PROJECT_EXPLORER = CapabilityId("rcplus.project-explorer")
    val ROBOT_MANAGER = CapabilityId("rcplus.robot-manager")
    val COMMAND_WINDOW = CapabilityId("rcplus.command-window")
    val IO_MONITOR = CapabilityId("rcplus.io-monitor")
    val TASK_MANAGER = CapabilityId("rcplus.task-manager")
    val BUILD_RUN_STATUS = CapabilityId("rcplus.build-run-status")
}
```

Only capabilities already verified in `docs/research/RCPLUS-7-COVERAGE.md` belong in this baseline.

- [ ] **Step 4: Implement the language and project-format descriptors**

Create `SpelPlusLanguageAdapter.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.ProgrammingLanguageAdapter
import mx.youteachtk.epsonrasimulator.adapters.ProgrammingLanguageAdapterId

object SpelPlusLanguageAdapter : ProgrammingLanguageAdapter {
    override val id = ProgrammingLanguageAdapterId("epson-spel-plus")
    override val displayName = "SPEL+"
}
```

Create `RcPlusProjectFormatAdapter.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.ProjectFormatAdapter
import mx.youteachtk.epsonrasimulator.adapters.ProjectFormatAdapterId

object RcPlusProjectFormatAdapter : ProjectFormatAdapter {
    override val id = ProjectFormatAdapterId("epson-rcplus-7-project")
    override val displayName = "EPSON RC+ 7 Project"
    override val fileExtensions = setOf("sprj", "prg", "inc", "pts", "mac")
}
```

- [ ] **Step 5: Implement the RC+ 7.5.3 simulator descriptor**

Create `RcPlus7SimulatorAdapter.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapter
import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapterId
import mx.youteachtk.epsonrasimulator.runtime.CapabilitySet
import mx.youteachtk.epsonrasimulator.runtime.TrainingProfileId

object RcPlus7SimulatorAdapter : SimulatorAdapter {
    override val id = SimulatorAdapterId("epson-rcplus-7.5.3")
    override val displayName = "EPSON RC+ 7.0 v7.5.3"
    override val programmingLanguageId = SpelPlusLanguageAdapter.id
    override val projectFormatId = RcPlusProjectFormatAdapter.id
    override val defaultProfileId = TrainingProfileId("school-setup")
    override val capabilities = CapabilitySet(
        setOf(
            RcPlusCapabilities.PROJECT_EXPLORER,
            RcPlusCapabilities.ROBOT_MANAGER,
            RcPlusCapabilities.COMMAND_WINDOW,
            RcPlusCapabilities.IO_MONITOR,
            RcPlusCapabilities.TASK_MANAGER,
            RcPlusCapabilities.BUILD_RUN_STATUS
        )
    )
}
```

- [ ] **Step 6: Re-run the adapter baseline test**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.adapters.AdapterRegistryTest.rcPlus7BaselineResolvesSpelAndProjectFormat" --stacktrace
```

Expected: PASS.

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/adapters/rcplus app/src/test/java/mx/youteachtk/epsonrasimulator/adapters/AdapterRegistryTest.kt
git commit -m "feat: register RC+ 7 SPEL+ baseline adapters"
```

---

### Task 5: Add canonical SharedRuntime state and command reducer

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/SharedRuntimeState.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/RuntimeCommand.kt`
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/SharedRuntime.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/SharedRuntimeTest.kt`

**Interfaces:**
- Consumes:
  - `RobotRegistry`
  - adapter/profile IDs
  - existing `JointState`, `TeachPoint`
- Produces:
  - immutable `SharedRuntimeState`
  - `RuntimeCommand` sealed interface
  - `SharedRuntime.dispatch(command)`
  - `SharedRuntime.subscribe(listener)`
  - `SharedRuntime.activeRobot()`

- [ ] **Step 1: Write failing runtime behavior tests**

Create `SharedRuntimeTest.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapterId
import mx.youteachtk.epsonrasimulator.domain.CartesianPose
import mx.youteachtk.epsonrasimulator.domain.TeachPoint
import mx.youteachtk.epsonrasimulator.robot.EpsonRobotProvider
import mx.youteachtk.epsonrasimulator.robot.RobotRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SharedRuntimeTest {
    private fun runtime(): SharedRuntime {
        val robots = RobotRegistry(listOf(EpsonRobotProvider))
        return SharedRuntime(
            robots = robots,
            initialState = SharedRuntimeState(
                simulatorAdapterId = SimulatorAdapterId("epson-rcplus-7.5.3"),
                trainingProfileId = TrainingProfileId("school-setup"),
                activeRobotId = "epson-c4-a601s",
                jointState = robots.require("epson-c4-a601s").zeroState()
            )
        )
    }

    @Test
    fun jointCommandUsesRobotLimits() {
        val runtime = runtime()

        runtime.dispatch(RuntimeCommand.SetJointValue(index = 0, value = 999.0))

        assertEquals(170.0, runtime.state.jointState[0], 0.0)
    }

    @Test
    fun resetRestoresRobotZeroState() {
        val runtime = runtime()
        runtime.dispatch(RuntimeCommand.SetJointValue(index = 1, value = -40.0))

        runtime.dispatch(RuntimeCommand.ResetJoints)

        assertEquals(
            runtime.activeRobot().zeroState(),
            runtime.state.jointState
        )
    }

    @Test
    fun saveTeachPointUpdatesCanonicalState() {
        val runtime = runtime()
        val point = TeachPoint(
            name = "P1",
            pose = CartesianPose(100.0, 200.0, 300.0)
        )

        runtime.dispatch(RuntimeCommand.SaveTeachPoint(point))

        assertEquals(point, runtime.state.teachPoints["P1"])
    }

    @Test
    fun subscribersReceiveInitialAndChangedState() {
        val runtime = runtime()
        val observed = mutableListOf<SharedRuntimeState>()

        val subscription = runtime.subscribe { observed += it }
        runtime.dispatch(RuntimeCommand.SetJointValue(index = 0, value = 20.0))
        subscription.cancel()

        assertEquals(2, observed.size)
        assertTrue(observed.last().jointState[0] == 20.0)
    }

    @Test(expected = IllegalStateException::class)
    fun nonLocalConnectionModesCannotExecuteInThisPhase() {
        val runtime = runtime()

        runtime.dispatch(
            RuntimeCommand.SetConnectionMode(ConnectionMode.RCPLUS_DIGITAL_TWIN)
        )
    }
}
```

- [ ] **Step 2: Run the focused test and verify failure**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.runtime.SharedRuntimeTest" --stacktrace
```

Expected: FAIL because runtime state/commands/store do not exist.

- [ ] **Step 3: Implement immutable runtime state**

Create `SharedRuntimeState.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.adapters.SimulatorAdapterId
import mx.youteachtk.epsonrasimulator.domain.JointState
import mx.youteachtk.epsonrasimulator.domain.TeachPoint

data class SharedRuntimeState(
    val simulatorAdapterId: SimulatorAdapterId,
    val trainingProfileId: TrainingProfileId,
    val activeRobotId: String,
    val jointState: JointState,
    val teachPoints: Map<String, TeachPoint> = emptyMap(),
    val connectionMode: ConnectionMode = ConnectionMode.LOCAL_SIMULATION
)
```

- [ ] **Step 4: Implement runtime commands**

Create `RuntimeCommand.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.domain.TeachPoint

sealed interface RuntimeCommand {
    data class SelectRobot(val robotId: String) : RuntimeCommand
    data class SetJointValue(val index: Int, val value: Double) : RuntimeCommand
    data class SetJointState(val values: List<Double>) : RuntimeCommand
    data object ResetJoints : RuntimeCommand
    data class SaveTeachPoint(val point: TeachPoint) : RuntimeCommand
    data class RemoveTeachPoint(val name: String) : RuntimeCommand
    data class SetConnectionMode(val mode: ConnectionMode) : RuntimeCommand
}
```

- [ ] **Step 5: Implement SharedRuntime**

Create `SharedRuntime.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.domain.RobotDefinition
import mx.youteachtk.epsonrasimulator.robot.RobotRegistry

class SharedRuntime(
    private val robots: RobotRegistry,
    initialState: SharedRuntimeState
) {
    private val listeners = linkedSetOf<(SharedRuntimeState) -> Unit>()

    var state: SharedRuntimeState = initialState
        private set

    init {
        val robot = robots.require(initialState.activeRobotId)
        require(initialState.jointState.values.size == robot.joints.size) {
            "Initial joint state does not match active robot"
        }
        check(initialState.connectionMode == ConnectionMode.LOCAL_SIMULATION) {
            "Only Local Simulation is executable in this phase"
        }
    }

    fun activeRobot(): RobotDefinition = robots.require(state.activeRobotId)

    fun dispatch(command: RuntimeCommand): SharedRuntimeState {
        val current = state
        val next = when (command) {
            is RuntimeCommand.SelectRobot -> {
                val robot = robots.require(command.robotId)
                current.copy(
                    activeRobotId = robot.id,
                    jointState = robot.zeroState()
                )
            }

            is RuntimeCommand.SetJointValue -> {
                val robot = activeRobot()
                require(command.index in robot.joints.indices) {
                    "Joint index out of range: ${command.index}"
                }
                val values = current.jointState.values.toMutableList()
                values[command.index] = robot.joints[command.index].clamp(command.value)
                current.copy(jointState = robot.validatedState(values))
            }

            is RuntimeCommand.SetJointState ->
                current.copy(jointState = activeRobot().validatedState(command.values))

            RuntimeCommand.ResetJoints ->
                current.copy(jointState = activeRobot().zeroState())

            is RuntimeCommand.SaveTeachPoint ->
                current.copy(
                    teachPoints = current.teachPoints + (command.point.name to command.point)
                )

            is RuntimeCommand.RemoveTeachPoint ->
                current.copy(teachPoints = current.teachPoints - command.name)

            is RuntimeCommand.SetConnectionMode -> {
                check(command.mode == ConnectionMode.LOCAL_SIMULATION) {
                    "Only Local Simulation is executable in this phase"
                }
                current.copy(connectionMode = command.mode)
            }
        }

        if (next != current) {
            state = next
            listeners.toList().forEach { it(next) }
        }

        return state
    }

    fun subscribe(listener: (SharedRuntimeState) -> Unit): RuntimeSubscription {
        listeners += listener
        listener(state)
        return RuntimeSubscription { listeners -= listener }
    }
}

class RuntimeSubscription(
    private val onCancel: () -> Unit
) {
    private var cancelled = false

    fun cancel() {
        if (!cancelled) {
            cancelled = true
            onCancel()
        }
    }
}
```

- [ ] **Step 6: Re-run the runtime tests**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.runtime.SharedRuntimeTest" --stacktrace
```

Expected: PASS.

- [ ] **Step 7: Run all unit tests to detect regressions**

```bash
gradle :app:testDebugUnitTest --stacktrace
```

Expected: all existing C4/catalog/kinematics tests plus the new runtime tests PASS.

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/runtime app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/SharedRuntimeTest.kt
git commit -m "feat: add canonical shared runtime state"
```

---

### Task 6: Add AppRuntimeFactory as the single composition root

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/AppRuntimeFactory.kt`
- Test: `app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/AppRuntimeFactoryTest.kt`

**Interfaces:**
- Consumes:
  - `EpsonRobotProvider`
  - `RcPlus7SimulatorAdapter`
  - `SpelPlusLanguageAdapter`
  - `RcPlusProjectFormatAdapter`
- Produces:
  - `AppRuntimeBundle`
  - `AppRuntimeFactory.createDefault()`

- [ ] **Step 1: Write the failing default-composition test**

Create `AppRuntimeFactoryTest.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import org.junit.Assert.assertEquals
import org.junit.Test

class AppRuntimeFactoryTest {
    @Test
    fun defaultRuntimeUsesVerifiedSchoolBaseline() {
        val bundle = AppRuntimeFactory.createDefault()

        assertEquals("epson-c4-a601s", bundle.runtime.state.activeRobotId)
        assertEquals(
            "epson-rcplus-7.5.3",
            bundle.runtime.state.simulatorAdapterId.value
        )
        assertEquals(
            "school-setup",
            bundle.runtime.state.trainingProfileId.value
        )
        assertEquals(
            ConnectionMode.LOCAL_SIMULATION,
            bundle.runtime.state.connectionMode
        )
        assertEquals(6, bundle.runtime.activeRobot().joints.size)
    }
}
```

- [ ] **Step 2: Run the focused test and verify failure**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.runtime.AppRuntimeFactoryTest" --stacktrace
```

Expected: FAIL because `AppRuntimeFactory` does not exist.

- [ ] **Step 3: Implement the composition root**

Create `AppRuntimeFactory.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.runtime

import mx.youteachtk.epsonrasimulator.adapters.AdapterRegistry
import mx.youteachtk.epsonrasimulator.adapters.rcplus.RcPlus7SimulatorAdapter
import mx.youteachtk.epsonrasimulator.adapters.rcplus.RcPlusProjectFormatAdapter
import mx.youteachtk.epsonrasimulator.adapters.rcplus.SpelPlusLanguageAdapter
import mx.youteachtk.epsonrasimulator.robot.EpsonRobotProvider
import mx.youteachtk.epsonrasimulator.robot.RobotRegistry

data class AppRuntimeBundle(
    val runtime: SharedRuntime,
    val robots: RobotRegistry,
    val adapters: AdapterRegistry
)

object AppRuntimeFactory {
    fun createDefault(): AppRuntimeBundle {
        val robots = RobotRegistry(
            providers = listOf(EpsonRobotProvider)
        )

        val adapters = AdapterRegistry(
            simulators = listOf(RcPlus7SimulatorAdapter),
            languages = listOf(SpelPlusLanguageAdapter),
            projectFormats = listOf(RcPlusProjectFormatAdapter)
        )

        val robot = robots.require("epson-c4-a601s")
        val simulator = adapters.requireSimulator(RcPlus7SimulatorAdapter.id)

        val runtime = SharedRuntime(
            robots = robots,
            initialState = SharedRuntimeState(
                simulatorAdapterId = simulator.id,
                trainingProfileId = simulator.defaultProfileId,
                activeRobotId = robot.id,
                jointState = robot.zeroState(),
                connectionMode = ConnectionMode.LOCAL_SIMULATION
            )
        )

        return AppRuntimeBundle(
            runtime = runtime,
            robots = robots,
            adapters = adapters
        )
    }
}
```

- [ ] **Step 4: Re-run the default-composition test**

```bash
gradle :app:testDebugUnitTest --tests "mx.youteachtk.epsonrasimulator.runtime.AppRuntimeFactoryTest" --stacktrace
```

Expected: PASS.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/runtime/AppRuntimeFactory.kt app/src/test/java/mx/youteachtk/epsonrasimulator/runtime/AppRuntimeFactoryTest.kt
git commit -m "feat: compose default school runtime"
```

---

### Task 7: Bind Compose to SharedRuntime and remove local authoritative joint state

**Files:**
- Create: `app/src/main/java/mx/youteachtk/epsonrasimulator/ui/RuntimeStateBinding.kt`
- Modify: `app/src/main/java/mx/youteachtk/epsonrasimulator/ui/RobotTrainerScreen.kt`
- Modify: `app/src/main/java/mx/youteachtk/epsonrasimulator/MainActivity.kt`

**Interfaces:**
- Consumes:
  - `SharedRuntime`
  - `RuntimeCommand`
  - `AppRuntimeFactory`
- Produces:
  - `@Composable fun rememberRuntimeState(runtime: SharedRuntime): SharedRuntimeState`
  - `@Composable fun RobotTrainerScreen(runtime: SharedRuntime)`
- Existing `C4RobotScene(jointValues: List<Float>, ...)` remains unchanged.

- [ ] **Step 1: Add the runtime-to-Compose binding**

Create `RuntimeStateBinding.kt`:

```kotlin
package mx.youteachtk.epsonrasimulator.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import mx.youteachtk.epsonrasimulator.runtime.SharedRuntime
import mx.youteachtk.epsonrasimulator.runtime.SharedRuntimeState

@Composable
fun rememberRuntimeState(runtime: SharedRuntime): SharedRuntimeState {
    var state by remember(runtime) { mutableStateOf(runtime.state) }

    DisposableEffect(runtime) {
        val subscription = runtime.subscribe { state = it }
        onDispose { subscription.cancel() }
    }

    return state
}
```

- [ ] **Step 2: Change RobotTrainerScreen to accept SharedRuntime**

Replace:

```kotlin
@Composable
fun RobotTrainerScreen() {
    val robot = EpsonRobotCatalog.C4_A601S
    val jointValues = remember(robot.id) {
        mutableStateListOf<Float>().apply {
            addAll(robot.zeroJointValues.map(Double::toFloat))
        }
    }
```

with:

```kotlin
@Composable
fun RobotTrainerScreen(runtime: SharedRuntime) {
    val runtimeState = rememberRuntimeState(runtime)
    val robot = runtime.activeRobot()
    val jointValues = runtimeState.jointState.values.map(Double::toFloat)
```

Remove imports for:
- `mutableStateListOf`
- `remember`
- `EpsonRobotCatalog`

Add imports for:
- `mx.youteachtk.epsonrasimulator.runtime.RuntimeCommand`
- `mx.youteachtk.epsonrasimulator.runtime.SharedRuntime`

- [ ] **Step 3: Route joint-slider edits through RuntimeCommand**

Change each slider callback from:

```kotlin
onValueChange = { jointValues[index] = it }
```

to:

```kotlin
onValueChange = {
    runtime.dispatch(
        RuntimeCommand.SetJointValue(
            index = index,
            value = it.toDouble()
        )
    )
}
```

- [ ] **Step 4: Route ZERO JOINTS through SharedRuntime**

Replace the loop that mutates `jointValues` with:

```kotlin
runtime.dispatch(RuntimeCommand.ResetJoints)
```

- [ ] **Step 5: Route RC+ TEST POSE through SharedRuntime**

Replace the calibration-pose mutation loop with:

```kotlin
runtime.dispatch(
    RuntimeCommand.SetJointState(
        C4Kinematics.calibrationPoseDegrees
    )
)
```

Keep the existing calibration disclaimer and labels unchanged because the RC+ Cartesian mapping is still unvalidated.

- [ ] **Step 6: Create one runtime at the app composition root**

Modify `MainActivity.kt` so `setContent` remembers one runtime instance:

```kotlin
package mx.youteachtk.epsonrasimulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import mx.youteachtk.epsonrasimulator.runtime.AppRuntimeFactory
import mx.youteachtk.epsonrasimulator.ui.RobotTrainerScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val runtime = remember {
                AppRuntimeFactory.createDefault().runtime
            }

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    RobotTrainerScreen(runtime = runtime)
                }
            }
        }
    }
}
```

This makes the app root—not the screen—the lifetime owner of the canonical runtime.

- [ ] **Step 7: Compile and run all unit tests**

Run:

```bash
gradle testDebugUnitTest --stacktrace
```

Expected: PASS.

- [ ] **Step 8: Build the debug APK**

Run:

```bash
gradle assembleDebug --stacktrace
```

Expected: BUILD SUCCESSFUL and `app/build/outputs/apk/debug/app-debug.apk` exists.

- [ ] **Step 9: Manual smoke test on Android device/emulator**

Verify all of the following against the pre-refactor behavior:

1. App opens to the current C4 trainer screen.
2. C4 model renders.
3. J1-J6 sliders move the same articulated links as before.
4. Joint values still respect existing limits.
5. ZERO JOINTS returns all joints to zero.
6. RC+ TEST POSE sets `20, -20, 30, 25, 15, 40`.
7. TCP/FLANGE calibration readout continues updating.
8. Camera orbit and pinch zoom still work.
9. No RC+ Digital Twin or Real Hardware behavior is exposed.

- [ ] **Step 10: Commit**

```bash
git add app/src/main/java/mx/youteachtk/epsonrasimulator/MainActivity.kt app/src/main/java/mx/youteachtk/epsonrasimulator/ui/RuntimeStateBinding.kt app/src/main/java/mx/youteachtk/epsonrasimulator/ui/RobotTrainerScreen.kt
git commit -m "refactor: drive C4 trainer from shared runtime"
```

---

### Task 8: Record foundation status and perform final verification

**Files:**
- Modify: `docs/ROADMAP.md`
- Modify: `docs/ARCHITECTURE.md`
- Modify: GitHub Issue #1 after code is pushed.

**Interfaces:**
- Consumes: completed runtime/provider/adapter foundation.
- Produces: repository documentation that accurately states what is implemented versus only designed.

- [ ] **Step 1: Update ROADMAP with the completed foundation milestone**

Add a milestone entry that states only these implemented facts:

```markdown
### Shared Runtime Foundation
- neutral RobotProvider/RobotRegistry added;
- simulator/language/project-format adapter contracts added;
- RC+ 7.0 v7.5.3 + SPEL+ baseline registered;
- canonical SharedRuntime owns current C4 robot selection, joints and teach points;
- current C4 Compose screen now dispatches robot-state changes through SharedRuntime;
- Local Simulation remains the only executable connection mode.
```

Do not mark SPEL+ parsing, I/O runtime, task runtime, RC+ MDI workspace, workcell actors, bridge, or real-hardware control as implemented.

- [ ] **Step 2: Update ARCHITECTURE with actual package names**

Under the concise overview, add the exact implemented packages:
- `robot`
- `runtime`
- `adapters`
- `adapters.rcplus`

Keep the formal design spec as the authoritative architecture source.

- [ ] **Step 3: Run final unit-test verification**

```bash
gradle testDebugUnitTest --stacktrace
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Run final APK verification**

```bash
gradle assembleDebug --stacktrace
```

Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Inspect the final diff before commit**

```bash
git diff --check
git status --short
```

Expected:
- `git diff --check` produces no whitespace errors.
- status contains only files intentionally changed by this plan.

- [ ] **Step 6: Commit documentation**

```bash
git add docs/ROADMAP.md docs/ARCHITECTURE.md
git commit -m "docs: record shared runtime foundation"
```

- [ ] **Step 7: Push the feature branch and verify GitHub Actions**

Push the execution branch, open/update its PR, and verify the Android CI job runs:
- Unit tests: `gradle testDebugUnitTest --stacktrace`
- Debug build: `gradle assembleDebug --stacktrace`

Do not claim the phase complete until the workflow is green.

- [ ] **Step 8: Update Issue #1 only after green CI**

Mark the Shared Runtime Foundation implementation milestone complete and link the PR/merge commit. Do not mark later sequence items complete.

---

## Self-Review

### 1. Spec coverage for this sub-project

Covered in this foundation plan:
- neutral Shared Runtime ownership;
- RobotProvider / RobotDefinition extension boundary;
- SimulatorAdapter boundary;
- ProgrammingLanguageAdapter identity boundary;
- ProjectFormatAdapter identity boundary;
- School Setup baseline identity;
- Local Simulation authority;
- migration away from UI-owned authoritative robot state;
- future multi-robot/multi-simulator compatibility without implementing speculative second vendors.

Deliberately delegated to later executable plans:
- source-preserving SPEL+ parser/document engine;
- TaskRuntime;
- IoRuntime;
- SimulationClock;
- functional WorkcellRuntime;
- RC+ MDI/window/command shell;
- persistence/round-trip storage;
- Windows bridge;
- optional Full Learning modules.

### 2. Placeholder scan

The executable steps contain no unresolved implementation placeholders. Later subsystems are explicitly excluded from this plan and sequenced in `2026-09-16-implementation-sequence.md` rather than represented as empty code.

### 3. Type consistency

The plan consistently uses:
- `RobotProvider` / `RobotRegistry`;
- `SimulatorAdapterId`;
- `ProgrammingLanguageAdapterId`;
- `ProjectFormatAdapterId`;
- `TrainingProfileId`;
- `CapabilityId` / `CapabilitySet`;
- `SharedRuntimeState`;
- `RuntimeCommand`;
- `SharedRuntime`;
- `RuntimeSubscription`;
- `AppRuntimeFactory.createDefault()`.

## Completion Evidence Required

Before this plan can be called complete, provide fresh evidence for:
- focused new unit tests passing;
- full `gradle testDebugUnitTest --stacktrace` passing;
- `gradle assembleDebug --stacktrace` passing;
- manual C4 smoke test passing;
- GitHub Actions green on the feature branch/PR;
- no claim of SPEL+ execution, digital-twin bridge, or real-hardware control.

