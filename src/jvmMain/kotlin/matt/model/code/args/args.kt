package matt.model.code.args

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer
import matt.lang.require.requireNot
import matt.model.code.args.mydecoder.MyAbstractCompositeDecoder
import matt.model.code.args.mydecoder.MyAbstractDecoder
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
        val decoder = ArgumentDecoder(ArgumentReader(args, deserializer.descriptor), deserializer.descriptor)
        val r = deserializer.deserialize(decoder)
        return r
    }

    inline fun <reified T> decodeFromArgs(
        args: Array<String>,
    ): T = decodeFromArgs(serializersModule.serializer(), args)


}


const val DEBUG = true


internal class ArgumentReader(
    private val args: Array<String>,
    private val descriptor: SerialDescriptor
) {
    private val itr = args.asList().listIterator()

    fun nextRaw(): String {
        if (!itr.hasNext()) {
            val argIndex = itr.nextIndex()
            val elementName = descriptor.getElementName(argIndex)
            val message = "please give value for $elementName (arg index = ${argIndex})"
            if (DEBUG) {
                println("DEBUG is true, so throwing an exception! If this was a user-error and DEBUG was false, this would just print the message and exit.")
                throw Exception(message)
            } else {
                println(message)
                exitProcess(1)
            }
        }
        return itr.next()
    }


    fun requireNoMoreArgs() {
        require(!itr.hasNext()) {
            "there should only be ${descriptor.elementsCount} arg(s)"
        }
    }

    fun numberOfRemainingArguments() = args.size - itr.nextIndex()

    fun nextIndexOrNull() = if (itr.hasNext()) itr.nextIndex() else null

}


internal class ArgumentDecoder(
    private val argReader: ArgumentReader,
    private val descriptor: SerialDescriptor
) : MyAbstractDecoder() {
    override fun beginStructure(descriptor: SerialDescriptor): MyAbstractCompositeDecoder {
        return ArgumentCompositeDecoder(this, argReader, descriptor)
    }

    override val serializersModule = ArgsSerializerModule

    override fun decodeString() = argReader.nextRaw()

    override fun decodeBoolean() = argReader.nextRaw().toBooleanStrict()

    override fun decodeChar() = argReader.nextRaw().single()

    override fun decodeLong() = argReader.nextRaw().toLong()

    override fun decodeDouble() = argReader.nextRaw().toDouble()

    override fun decodeFloat() = argReader.nextRaw().toFloat()

    override fun decodeInt() = argReader.nextRaw().toInt()


    override fun decodeShort() = argReader.nextRaw().toShort()


}


internal class ArgumentCompositeDecoder(
    decoder: ArgumentDecoder,
    private val argReader: ArgumentReader,
    private val descriptor: SerialDescriptor
) : MyAbstractCompositeDecoder(
    serializersModule = decoder.serializersModule,
    decoder = decoder
) {

    private val originalSize = argReader.numberOfRemainingArguments()

    private var decodingVararg = false


    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        requireNot(decodingVararg) {
            "can only decode vararg once"
        }
        decodingVararg = true
        val r = argReader.numberOfRemainingArguments()
        return r
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        return argReader.nextIndexOrNull() ?: DECODE_DONE
    }

    override fun decodeSequentially(): Boolean {
        return originalSize == descriptor.elementsCount
    }

}


private val ArgsSerializerModule = EmptySerializersModule()