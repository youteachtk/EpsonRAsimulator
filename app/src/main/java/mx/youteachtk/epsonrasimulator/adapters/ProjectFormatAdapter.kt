package mx.youteachtk.epsonrasimulator.adapters

interface ProjectFormatAdapter {
    val id: ProjectFormatAdapterId
    val displayName: String
    val fileExtensions: Set<String>
}
