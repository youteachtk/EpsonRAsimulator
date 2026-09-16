package mx.youteachtk.epsonrasimulator.adapters.rcplus

import mx.youteachtk.epsonrasimulator.adapters.ProjectFormatAdapter
import mx.youteachtk.epsonrasimulator.adapters.ProjectFormatAdapterId

object RcPlusProjectFormatAdapter : ProjectFormatAdapter {
    override val id = ProjectFormatAdapterId("epson-rcplus-7-project")
    override val displayName = "EPSON RC+ 7 Project"
    override val fileExtensions = setOf("sprj", "prg", "inc", "pts", "mac")
}
