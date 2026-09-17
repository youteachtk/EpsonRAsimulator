package mx.youteachtk.epsonrasimulator.adapters.rcplus.project

import mx.youteachtk.epsonrasimulator.project.NativeKnownEditable
import mx.youteachtk.epsonrasimulator.project.NativeKnownPreserved
import mx.youteachtk.epsonrasimulator.project.NativeOpaque
import mx.youteachtk.epsonrasimulator.project.NativeResourceKind
import mx.youteachtk.epsonrasimulator.project.ProjectResource
import mx.youteachtk.epsonrasimulator.project.ProjectResourceClassifier

object RcPlusResourceClassifier : ProjectResourceClassifier {
    override fun classify(path: String, bytes: ByteArray): ProjectResource {
        val basename = path
            .substringAfterLast('/')
            .substringAfterLast('\\')
        val lowerBasename = basename.lowercase()
        val extension = lowerBasename.substringAfterLast('.', missingDelimiterValue = "")

        return when {
            extension == "prg" ->
                NativeKnownEditable(path, NativeResourceKind.PROGRAM, bytes)

            extension == "inc" ->
                NativeKnownEditable(path, NativeResourceKind.INCLUDE, bytes)

            extension == "pts" ->
                NativeKnownPreserved(path, NativeResourceKind.POINTS, bytes)

            extension == "mac" ->
                NativeKnownPreserved(path, NativeResourceKind.MACRO, bytes)

            lowerBasename == "iolabel.dat" ->
                NativeKnownPreserved(path, NativeResourceKind.IO_LABELS, bytes)

            lowerBasename == "usererrors.dat" ->
                NativeKnownPreserved(path, NativeResourceKind.USER_ERRORS, bytes)

            extension == "sprj" ->
                NativeKnownPreserved(path, NativeResourceKind.PROJECT_DESCRIPTOR, bytes)

            else -> NativeOpaque(path, bytes)
        }
    }
}
