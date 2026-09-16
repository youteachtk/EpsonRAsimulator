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
