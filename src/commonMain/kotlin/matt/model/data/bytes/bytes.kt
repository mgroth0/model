package matt.model.data.bytes

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import matt.lang.assertions.require.requireEquals
import matt.lang.assertions.require.requireZero

fun ByteArray.toView(
    offset: Int = 0,
    length: Int = size
) = BytesView(offset = offset, length = length, array = this)


object BytesViewSerializer : KSerializer<BytesView> {


    private val byteArraySerializer by lazy {
        serializer<ByteArray>()
    }

    override val descriptor by lazy {
        byteArraySerializer.descriptor
    }


    override fun serialize(
        encoder: Encoder,
        value: BytesView
    ) {
        encoder.encodeSerializableValue(byteArraySerializer, value.extractByteArray())
    }

    override fun deserialize(decoder: Decoder): BytesView {
        val byteArray = decoder.decodeSerializableValue(byteArraySerializer)
        return byteArray.toView()
    }


}

@Serializable(with = BytesViewSerializer::class)
class BytesView(
    val array: ByteArray,
    val offset: Int,
    val length: Int
) {
    fun asByteArray(): ByteArray {
        requireZero(offset) {
            "can only use asByteArray if offset is 0"
        }
        requireEquals(length, array.size) {
            "can only use asByteArray if length is array.size"
        }
        return array
    }

    val size get() = length


    fun extractByteArray(): ByteArray = if (offset == 0 && length == array.size) {
        asByteArray()
    } else {
        array.copyOfRange(offset, offset + length)
    }

}
