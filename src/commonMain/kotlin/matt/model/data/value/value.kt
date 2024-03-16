package matt.model.data.value

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import matt.lang.model.value.MyValueClass


abstract class MyValueClassSerializer<T, V : MyValueClass<T>>(
    private val elementSerializer: KSerializer<T>
) : KSerializer<V> {
    final override val descriptor = elementSerializer.descriptor
    final override fun serialize(
        encoder: Encoder,
        value: V
    ) {
        encoder.encodeSerializableValue(
            elementSerializer, value.value
        )
    }

    final override fun deserialize(decoder: Decoder): V {
        val innerValue = decoder.decodeSerializableValue(elementSerializer)
        return construct(value = innerValue)
    }

    abstract fun construct(value: T): V
}
