
package matt.model.data.bytes

import kotlinx.io.bytestring.ByteString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import matt.prim.bytestr.toByteString

object ByteStringSerializer: KSerializer<ByteString> {
    private val byteArraySerializer = serializer<ByteArray>()
    override fun deserialize(decoder: Decoder): ByteString = decoder.decodeSerializableValue(byteArraySerializer).toByteString()

    override val descriptor = serialDescriptor<ByteArray>()

    override fun serialize(
        encoder: Encoder,
        value: ByteString
    ) {
        encoder.encodeSerializableValue(byteArraySerializer, value.toByteArray())
    }
}
