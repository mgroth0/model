package matt.model.code.compat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/* I have at least 1 of these somewhere else. It's the perfect solution for quick serialization compatibility!!! */
interface CompatibilityModel<T> {
    fun modernize(): T
}

interface DefaultPluginSerializerIdea

abstract class CompatSerializer<T, C : CompatibilityModel<T>> : KSerializer<T> {
    final override fun deserialize(decoder: Decoder): T {
        val compat = decoder.decodeSerializableValue(pluginCompatSer)
        return compat.modernize()
    }

    final override val descriptor get() = pluginCompatSer.descriptor
    abstract val pluginCompatSer: KSerializer<C>
    abstract val pluginModernSer: KSerializer<T>

    final override fun serialize(
        encoder: Encoder,
        value: T
    ) {
        encoder.encodeSerializableValue(pluginModernSer, value)
    }
}
