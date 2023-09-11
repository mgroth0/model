package matt.model.code.args

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer
import matt.lang.require.requireNot
import kotlin.system.exitProcess


interface ArgumentsFormat : SerialFormat {
    fun <T> decodeFromArgs(
        deserializer: DeserializationStrategy<T>,
        args: Array<String>
    ): T
}


object Arguments : ArgumentsFormat {

    override val serializersModule = EmptySerializersModule()


    override fun <T> decodeFromArgs(
        deserializer: DeserializationStrategy<T>,
        args: Array<String>
    ): T {
        val decoder = ArgumentDecoder(args, deserializer.descriptor)
        val r = deserializer.deserialize(decoder)
        return r
    }

    inline fun <reified T> decodeFromArgs(
        args: Array<String>,
    ): T = decodeFromArgs(serializersModule.serializer(), args)


}


class ArgumentDecoder(
    private val args: Array<String>,
    private val descriptor: SerialDescriptor
) : AbstractDecoder() {
    override val serializersModule = ArgsSerializerModule

    private val itr = args.asList().listIterator()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {

        return if (itr.hasNext()) {
            itr.nextIndex()
        } else {
            DECODE_DONE
        }
    }

    override fun decodeSequentially() = true

    private var decodingVararg = false

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        requireNot(decodingVararg) {
            "can only decode vararg once"
        }
        decodingVararg = true
        return args.size - itr.nextIndex()
    }

    override fun decodeString() = nextRaw()

    override fun decodeBoolean() = nextRaw().toBooleanStrict()

    override fun decodeChar() = nextRaw().single()

    override fun decodeLong() = nextRaw().toLong()

    override fun decodeDouble() = nextRaw().toDouble()

    override fun decodeFloat() = nextRaw().toFloat()

    override fun decodeInt() = nextRaw().toInt()


    override fun decodeShort() = nextRaw().toShort()


    private fun nextRaw(): String {
        if (!itr.hasNext()) {
            val argIndex = itr.nextIndex()
            val elementName = descriptor.getElementName(argIndex)
            println("please give value for $elementName (arg index = ${argIndex})")
            exitProcess(1)
        }
        return itr.next()
    }

    internal fun requireNoMoreArgs() {
        require(!itr.hasNext()) {
            "there should only be ${descriptor.elementsCount} arg(s)"
        }
    }


}


private val ArgsSerializerModule = EmptySerializersModule()