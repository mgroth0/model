package matt.model.code.compat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/*I have at least 1 of these somewhere else. It's the perfect solution for quick serialization compatibility!!!*/
interface CompatibilityModel<T> {
    fun modernize(): T
}


interface DefaultPluginSerializerIdea


interface CompatSerializer<T, C : CompatibilityModel<T>> : KSerializer<T> {
    override fun deserialize(decoder: Decoder): T {
        val compat = decoder.decodeSerializableValue(pluginCompatSer)
        return compat.modernize()
    }

    override val descriptor get() = pluginCompatSer.descriptor
    val pluginCompatSer: KSerializer<C>
    val pluginModernSer: KSerializer<T>
    override fun serialize(
        encoder: Encoder,
        value: T
    ) {
        encoder.encodeSerializableValue(pluginModernSer, value)
    }
}