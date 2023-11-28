package matt.model.data.range

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.prim.byte.toInt
import matt.prim.byte.toLong
import matt.prim.endian.MyByteOrder
import matt.prim.int.toByteArray
import matt.prim.long.toByteArray
import matt.prim.pw.endian.java


object IntRangeSerializer : KSerializer<IntRange> {

    private val BYTE_ORDER = MyByteOrder.BIG

    override val descriptor = serialDescriptor<Long>()

    override fun deserialize(decoder: Decoder): IntRange {
        val l = decoder.decodeLong()
        return IntRange(
            (l shr 32).toByteArray(BYTE_ORDER.java).toList().subList(4, 8).toByteArray().toInt(BYTE_ORDER),
            l.toByteArray(BYTE_ORDER.java).toList().subList(4, 8).toByteArray().toInt(BYTE_ORDER)
        )
    }

    override fun serialize(
        encoder: Encoder,
        value: IntRange
    ) {
        @Suppress("ReplaceRangeStartEndInclusiveWithFirstLast")
        encoder.encodeLong(
            (value.start.toByteArray(BYTE_ORDER) + value.endInclusive.toByteArray(BYTE_ORDER)).toLong(BYTE_ORDER)
        )
    }
}
