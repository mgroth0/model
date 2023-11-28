package matt.model.code.output

import java.io.OutputStream

enum class OutputType {
    STDOUT, STDERR
}

sealed interface OutputStreams {
    val out: OutputStream?
    val err: OutputStream?
}

interface ActualOutputStreams : OutputStreams {
    override val out: OutputStream
    override val err: OutputStream
}

data object None : OutputStreams {
    override val out = null
    override val err = null
}