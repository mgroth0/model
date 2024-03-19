@file:OptIn(ExperimentalSerializationApi::class)

package matt.model.code.args.mydecoder

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

/*

The purpose of this class is to be much more explicit and structured about about where I am conforming to AbstractDecoder, and where I am diverging from it.

The standard here is to only have fully abstract or final methods. Nothing open. This will help me have precise understanding of how these classes work, since `open` functions (which AbstractDecoder is full of) can be completely replaced which can be confusing for complex classes like this.

*/
abstract class MyAbstractDecoder : Decoder {
    private val abstractDecoderDelegate =
        object : AbstractDecoder() {
            override val serializersModule: SerializersModule get() = error("the outer implementation should be used")

            override fun decodeElementIndex(descriptor: SerialDescriptor) = error("the outer implementation should be used")
        }

    /* Note: AbstractDecoder implementation is to return itself */
    abstract override fun beginStructure(descriptor: SerialDescriptor): MyAbstractCompositeDecoder

    final override fun decodeByte(): Byte = abstractDecoderDelegate.decodeByte()

    final override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = abstractDecoderDelegate.decodeEnum(enumDescriptor)

    /* this is the exact AbstractDecoder implementation */
    final override fun decodeInline(descriptor: SerialDescriptor): Decoder = this

    @ExperimentalSerializationApi
    final override fun decodeNotNullMark(): Boolean = abstractDecoderDelegate.decodeNotNullMark()

    @ExperimentalSerializationApi
    final override fun decodeNull(): Nothing? = abstractDecoderDelegate.decodeNull()

    /* this is the exact implementation in Decoder and AbstractDecoder */
    final override fun <T : Any?> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T = deserializer.deserialize(this)

    /* this is the exact implementation in Decoder and AbstractDecoder */
    @ExperimentalSerializationApi
    final override fun <T : Any> decodeNullableSerializableValue(deserializer: DeserializationStrategy<T?>): T? {
        val isNullabilitySupported = deserializer.descriptor.isNullable
        return if (isNullabilitySupported || decodeNotNullMark()) decodeSerializableValue(deserializer) else decodeNull()
    }
}

abstract class MyAbstractCompositeDecoder(
    final override val serializersModule: SerializersModule,
    private val decoder: Decoder
) : CompositeDecoder {
    private val abstractDecoderDelegate =
        object : AbstractDecoder() {
            override val serializersModule: SerializersModule get() = error("the outer implementation should be used")

            override fun decodeElementIndex(descriptor: SerialDescriptor) = error("the outer implementation should be used")
        }

    /* based on AbstractDecoder implementation */
    final override fun decodeBooleanElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Boolean = decoder.decodeBoolean()

    /* based on AbstractDecoder implementation */
    final override fun decodeByteElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Byte = decoder.decodeByte()

    /* based on AbstractDecoder implementation */
    final override fun decodeCharElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Char = decoder.decodeChar()

    /* based on AbstractDecoder implementation */
    final override fun decodeDoubleElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Double = decoder.decodeDouble()

    /* based on AbstractDecoder implementation */
    final override fun decodeFloatElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Float = decoder.decodeFloat()

    /* based on AbstractDecoder implementation */
    final override fun decodeInlineElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Decoder = decoder.decodeInline(descriptor.getElementDescriptor(index))

    /* based on AbstractDecoder implementation */
    final override fun decodeIntElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Int = decoder.decodeInt()

    /* based on AbstractDecoder implementation */
    final override fun decodeLongElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Long = decoder.decodeLong()

    final override fun decodeShortElement(
        descriptor: SerialDescriptor,
        index: Int
    ): Short = decoder.decodeShort()

    final override fun decodeStringElement(
        descriptor: SerialDescriptor,
        index: Int
    ): String = decoder.decodeString()

    /* new open function not in Decoder, defined in AbstractDecoder. This is the default implementation, but it is open in AbstractDecoder. It is final here, which is better for now as it enforces more consistency. */
    private fun <T : Any?> decodeSerializableValue(
        deserializer: DeserializationStrategy<T>,
        @Suppress("UNUSED_PARAMETER") previousValue: T? = null
    ): T = decoder.decodeSerializableValue(deserializer)

    @ExperimentalSerializationApi
    final override fun <T : Any> decodeNullableSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T?>,
        previousValue: T?
    ): T? {
        /* currently, this is an exact copy of the AbstractDecoder implementation. It must at minimum be copied here to ensure I don't make calls directly to the delegate. */
        val isNullabilitySupported = deserializer.descriptor.isNullable
        return if (isNullabilitySupported || decoder.decodeNotNullMark()) {
            decodeSerializableValue(
                deserializer,
                previousValue
            )
        } else {
            decoder.decodeNull()
        }
    }

    /* based on AbstractDecoder implementation */
    final override fun <T> decodeSerializableElement(
        descriptor: SerialDescriptor,
        index: Int,
        deserializer: DeserializationStrategy<T>,
        previousValue: T?
    ): T = decodeSerializableValue(deserializer, previousValue)

    /* AbstractDecoder implementation does nothing */
    final override fun endStructure(descriptor: SerialDescriptor) {}
}
