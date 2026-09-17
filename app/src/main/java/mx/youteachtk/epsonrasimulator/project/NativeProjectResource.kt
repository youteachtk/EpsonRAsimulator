package mx.youteachtk.epsonrasimulator.project

enum class NativeResourceKind {
    PROGRAM,
    INCLUDE,
    POINTS,
    MACRO,
    IO_LABELS,
    USER_ERRORS,
    PROJECT_DESCRIPTOR,
    UNKNOWN
}

sealed interface ProjectResource {
    val path: String
    fun bytesCopy(): ByteArray
}

fun interface ProjectResourceClassifier {
    fun classify(path: String, bytes: ByteArray): ProjectResource
}

class NativeKnownEditable(
    override val path: String,
    val kind: NativeResourceKind,
    bytes: ByteArray
) : ProjectResource {
    private val content = bytes.copyOf()

    override fun bytesCopy(): ByteArray = content.copyOf()
}

class NativeKnownPreserved(
    override val path: String,
    val kind: NativeResourceKind,
    bytes: ByteArray
) : ProjectResource {
    private val content = bytes.copyOf()

    override fun bytesCopy(): ByteArray = content.copyOf()
}

class NativeOpaque(
    override val path: String,
    bytes: ByteArray
) : ProjectResource {
    val kind: NativeResourceKind = NativeResourceKind.UNKNOWN
    private val content = bytes.copyOf()

    override fun bytesCopy(): ByteArray = content.copyOf()
}

class AppSidecarMetadata(
    override val path: String,
    bytes: ByteArray
) : ProjectResource {
    private val content = bytes.copyOf()

    override fun bytesCopy(): ByteArray = content.copyOf()
}
