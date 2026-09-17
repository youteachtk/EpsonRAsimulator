package mx.youteachtk.epsonrasimulator.programming

data class SourceEdit(
    val range: SourceRange,
    val replacement: String
) {
    fun apply(source: String): String {
        require(range.endExclusive <= source.length) {
            "Source edit range exceeds source length"
        }

        return source.substring(0, range.start) +
            replacement +
            source.substring(range.endExclusive)
    }
}
