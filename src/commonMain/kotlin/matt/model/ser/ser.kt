@file:SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/532")
@file:SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/1630")
@file:SeeURL("https://github.com/Kotlin/kotlinx.serialization/issues/2182")

package matt.model.ser

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import matt.lang.anno.SeeURL
import matt.lang.classname.JvmQualifiedClassName
import matt.lang.classname.SimpleClassName
import matt.lang.model.file.UnsafeFilePath

/*Automatic serializer generation is not working for classes from another module. See links at top.*/
/*@Serializer(forClass = UnsafeFilePath::class)*/
object UnsafeFilePathSerializer : EncodedAsStringKSerializer<UnsafeFilePath>() {
    override fun String.decode() = UnsafeFilePath(this)
    override fun UnsafeFilePath.encodeToString() = path
}

object JvmQualifiedClassNameSerializer : EncodedAsStringKSerializer<JvmQualifiedClassName>() {
    override fun String.decode() = JvmQualifiedClassName(this)
    override fun JvmQualifiedClassName.encodeToString() = name
}

object SimpleClassNameSerializer : EncodedAsStringKSerializer<SimpleClassName>() {
    override fun String.decode() = SimpleClassName(this)
    override fun SimpleClassName.encodeToString() = name
}


abstract class EncodedAsStringKSerializer<T> : KSerializer<T> {
    companion object {
        private val DESCRIPTOR by lazy {
            serialDescriptor<String>()
        }
    }

    final override val descriptor = DESCRIPTOR
    final override fun deserialize(decoder: Decoder): T = decoder.decodeString().decode()

    final override fun serialize(
        encoder: Encoder,
        value: T
    ) {
        encoder.encodeString(value.encodeToString())
    }

    protected abstract fun T.encodeToString(): String
    protected abstract fun String.decode(): T
}


val ExternalSerializersModule by lazy {
    SerializersModule {
        contextual(UnsafeFilePathSerializer)
        contextual(JvmQualifiedClassNameSerializer)
        contextual(SimpleClassNameSerializer)
    }
}


data object NullableIntSerializer : KSerializer<Int?> {
    override val descriptor = serialDescriptor<Int?>()

    override fun deserialize(decoder: Decoder): Int? = decoder.decodeString().takeIf { it.isNotEmpty() }?.toInt()

    override fun serialize(
        encoder: Encoder,
        value: Int?
    ) {
        encoder.encodeString(value?.toString() ?: "")
    }

}

data object NullableDoubleSerializer : KSerializer<Double?> {
    override val descriptor = serialDescriptor<Double?>()

    override fun deserialize(decoder: Decoder): Double? = decoder.decodeString().takeIf { it.isNotEmpty() }?.toDouble()

    override fun serialize(
        encoder: Encoder,
        value: Double?
    ) {
        encoder.encodeString(value?.toString() ?: "")
    }

}


data object NullableStrictBooleanSerializer : KSerializer<Boolean?> {
    override val descriptor = serialDescriptor<Boolean?>()

    override fun deserialize(decoder: Decoder): Boolean? = decoder.decodeString().takeIf { it.isNotEmpty() }?.toBooleanStrict()

    override fun serialize(
        encoder: Encoder,
        value: Boolean?
    ) {
        encoder.encodeString(value?.toString() ?: "")
    }

}
