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
