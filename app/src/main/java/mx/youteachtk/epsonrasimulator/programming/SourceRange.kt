package mx.youteachtk.epsonrasimulator.programming

data class SourceRange(
    val start: Int,
    val endExclusive: Int
) {
    init {
        require(start >= 0) { "start must be non-negative" }
        require(endExclusive >= start) { "endExclusive must be >= start" }
    }

    val length: Int
        get() = endExclusive - start

    fun contains(offset: Int): Boolean =
        offset >= start && offset < endExclusive
}
