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
