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
import kotlinx.serialization.serializer
import matt.lang.anno.SeeURL
import matt.lang.classname.common.JvmQualifiedClassName
import matt.lang.classname.common.SimpleClassName
import matt.lang.model.file.UnsafeFilePath
import matt.prim.converters.StringConverter

/*Automatic serializer generation is not working for classes from another module. See links at top.


@Serializer(forClass = UnsafeFilePath::class)*/
object UnsafeFilePathSerializer : EncodedAsStringSerializer<UnsafeFilePath>() {
    override fun String.decode() = UnsafeFilePath(this)
    override fun UnsafeFilePath.encodeToString() = path
}

object JvmQualifiedClassNameSerializer : EncodedAsStringSerializer<JvmQualifiedClassName>() {
    override fun String.decode() = JvmQualifiedClassName(this)
    override fun JvmQualifiedClassName.encodeToString() = name
}

object SimpleClassNameSerializer : EncodedAsStringSerializer<SimpleClassName>() {
    override fun String.decode() = SimpleClassName(this)
    override fun SimpleClassName.encodeToString() = name
}


abstract class EncodedAsBytesSerializer<T>: KSerializer<T> {
    companion object {
        private val DESCRIPTOR by lazy {
            serialDescriptor<ByteArray>()
        }
        private val BYTE_ARRAY_SER by lazy {
            serializer<ByteArray>()
        }
    }


    final override val descriptor = DESCRIPTOR

    final override fun deserialize(decoder: Decoder): T = decoder.decodeSerializableValue(BYTE_ARRAY_SER).decode()

    final override fun serialize(
        encoder: Encoder,
        value: T
    ) {
        encoder.encodeSerializableValue(BYTE_ARRAY_SER, value.encodeToByteArray())
    }

    protected abstract fun T.encodeToByteArray(): ByteArray
    protected abstract fun ByteArray.decode(): T
}


abstract class EncodedAsStringSerializer<T> : KSerializer<T>, StringConverter<T> {
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

    final override fun fromString(s: String): T = s.decode()

    final override fun toString(t: T): String = t.encodeToString()
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
