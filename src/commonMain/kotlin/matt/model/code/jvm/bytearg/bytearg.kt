package matt.model.code.jvm.bytearg

import kotlinx.serialization.Serializable
import matt.lang.anno.Open
import matt.lang.assertions.require.requireNotIn
import matt.model.code.jvm.args.JvmArg
import matt.model.data.byte.ByteSize
import kotlin.jvm.JvmInline

interface JvmByteSizeArg : JvmArg {

    val key: String
    val size: ByteSize

    @Open
    override fun toRawArg(): String {
        requireNotIn("-", key)
        return "-$key${size.formattedBinaryNoSpaceNoDecimalsAndSingleLetterUnit}"
    }
}


@Serializable
@JvmInline
value class Xms(override val size: ByteSize) : JvmByteSizeArg {
    override val key get() = "Xms"
}

@Serializable
@JvmInline
value class Xmx(override val size: ByteSize) : JvmByteSizeArg {
    override val key get() = "Xmx"
}

@Serializable
@JvmInline
value class Xss(override val size: ByteSize) : JvmByteSizeArg {
    override val key get() = "Xss"
}
