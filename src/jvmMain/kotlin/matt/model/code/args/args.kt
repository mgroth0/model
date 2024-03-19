@file:OptIn(ExperimentalSerializationApi::class)

package matt.model.code.args

import arrow.core.Either
import arrow.core.Either.Left
import arrow.core.Either.Right
import arrow.core.getOrElse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.serializer
import matt.lang.assertions.require.requireNot
import matt.lang.idea.FailableIdea
import matt.model.code.args.mydecoder.MyAbstractCompositeDecoder
import matt.model.code.args.mydecoder.MyAbstractDecoder
import kotlin.system.exitProcess


interface ArgumentsFormat : SerialFormat {
    fun <T> decodeFromArgs(
        deserializer: DeserializationStrategy<T>,
        args: Array<String>
    ): Either<T, UserError>
}


object Arguments : ArgumentsFormat {

    override val serializersModule = EmptySerializersModule()


    inline fun <reified T> mainOrExitWithLogicalFailure(args: Array<String>, app: (T) -> Unit) {
        val goodArgs =
            decodeFromArgs<T>(args).swap().getOrElse {
                println(it.message)
                exitProcess(1)
            }
        app(goodArgs)
    }

    override fun <T> decodeFromArgs(
        deserializer: DeserializationStrategy<T>,
        args: Array<String>
    ): Either<T, UserError> {
        val decoder = ArgumentDecoder(ArgumentReader(args, deserializer.descriptor), deserializer.descriptor)
        try {
            val r = deserializer.deserialize(decoder)
            return Left(r)
        } catch (e: UserArgumentErrorException) {
            return Right(e.asUserError())
        }
    }

    inline fun <reified T> decodeFromArgs(
        args: Array<String>
    ): Either<T, UserError> = decodeFromArgs(serializersModule.serializer(), args)
}

/*Learned about term from https://arrow-kt.io/learn/typed-errors/working-with-typed-errors */
sealed interface LogicalFailure: FailableIdea

sealed interface UserError: LogicalFailure {
    val message: String
}
data class BadArguments(override val message: String): UserError

internal class UserArgumentErrorException(override val message: String): Exception(message) {
    fun asUserError() = BadArguments(message)
}

internal class ArgumentReader(
    private val args: Array<String>,
    private val descriptor: SerialDescriptor
) {
    private val itr = args.asList().listIterator()

    fun nextRaw(): String {
        if (!itr.hasNext()) {
            val argIndex = itr.nextIndex()
            val elementName = descriptor.getElementName(argIndex)
            val message = "please give value for $elementName (arg index = $argIndex)"
            throw UserArgumentErrorException(message)
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
    override fun beginStructure(
        descriptor: SerialDescriptor
    ): MyAbstractCompositeDecoder = ArgumentCompositeDecoder(this, argReader, descriptor)

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

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = argReader.nextIndexOrNull() ?: DECODE_DONE

    override fun decodeSequentially(): Boolean = originalSize == descriptor.elementsCount
}


private val ArgsSerializerModule = EmptySerializersModule()
