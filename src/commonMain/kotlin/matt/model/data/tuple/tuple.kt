package matt.model.data.tuple

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.json.JsonArray
import matt.lang.function.Consume


fun <T> List<T>.verifyToQuad(): UniformQuad<T> {
    check(size == 4)
    return Quad(this[0], this[1], this[2], this[3])
}

typealias UniformQuad<T> = Quad<T, T, T, T>


/*Critical so serialization of this is consistent with serialization of a list*/
class QuadSerializer<A, B, C, D>(
    private val aSerializer: KSerializer<A>,
    private val bSerializer: KSerializer<B>,
    private val cSerializer: KSerializer<C>,
    private val dSerializer: KSerializer<D>
) : KSerializer<Quad<A, B, C, D>> {

    override val descriptor: SerialDescriptor = serialDescriptor<JsonArray>()

    override fun deserialize(decoder: Decoder): Quad<A, B, C, D> {
        return decoder.decodeStructure(descriptor) {
            Quad(
                decodeSerializableElement(aSerializer.descriptor, 0, aSerializer),
                decodeSerializableElement(bSerializer.descriptor, 1, bSerializer),
                decodeSerializableElement(cSerializer.descriptor, 2, cSerializer),
                decodeSerializableElement(dSerializer.descriptor, 3, dSerializer)
            )
        }


    }


    override fun serialize(
        encoder: Encoder,
        value: Quad<A, B, C, D>
    ) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(aSerializer.descriptor, 0, aSerializer, value.first)
            encodeSerializableElement(bSerializer.descriptor, 1, bSerializer, value.second)
            encodeSerializableElement(cSerializer.descriptor, 2, cSerializer, value.third)
            encodeSerializableElement(dSerializer.descriptor, 3, dSerializer, value.fourth)
        }
    }
}


@Serializable(with = QuadSerializer::class)
data class Quad<out A, out B, out C, out D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) {

    override fun toString(): String = "($first, $second, $third, ${fourth})"

}


fun <T> UniformQuad<T>.toList() = listOf(first, second, third, fourth)
inline fun <reified T> UniformQuad<T>.toTypedArray() = toList().toTypedArray<T>()
operator fun <T> UniformQuad<T>.contains(value: T) = toList().contains(value)


fun <T, R> UniformQuad<T>.map(op: (T) -> R): UniformQuad<R> = Quad(op(first), op(second), op(third), op(fourth))

fun <T> UniformQuad<T>.forEach(op: Consume<T>) {
    op(first)
    op(second)
    op(third)
    op(fourth)
}


fun <T> List<T>.verifyToQuint(): UniformQuint<T> {
    check(size == 5)
    return Quint(this[0], this[1], this[2], this[3], this[4])
}

typealias UniformQuint<T> = Quint<T, T, T, T, T>

/*Critical so serialization of this is consistent with serialization of a list*/
class QuintSerializer<A, B, C, D, E>(
    private val aSerializer: KSerializer<A>,
    private val bSerializer: KSerializer<B>,
    private val cSerializer: KSerializer<C>,
    private val dSerializer: KSerializer<D>,
    private val eSerializer: KSerializer<E>
) : KSerializer<Quint<A, B, C, D, E>> {

    override val descriptor: SerialDescriptor = serialDescriptor<JsonArray>()

    override fun deserialize(decoder: Decoder): Quint<A, B, C, D, E> {
        return decoder.decodeStructure(descriptor) {
            Quint(
                decodeSerializableElement(aSerializer.descriptor, 0, aSerializer),
                decodeSerializableElement(bSerializer.descriptor, 1, bSerializer),
                decodeSerializableElement(cSerializer.descriptor, 2, cSerializer),
                decodeSerializableElement(dSerializer.descriptor, 3, dSerializer),
                decodeSerializableElement(eSerializer.descriptor, 4, eSerializer)
            )
        }


    }


    override fun serialize(
        encoder: Encoder,
        value: Quint<A, B, C, D, E>
    ) {
        encoder.encodeStructure(descriptor) {
            encodeSerializableElement(aSerializer.descriptor, 0, aSerializer, value.first)
            encodeSerializableElement(bSerializer.descriptor, 1, bSerializer, value.second)
            encodeSerializableElement(cSerializer.descriptor, 2, cSerializer, value.third)
            encodeSerializableElement(dSerializer.descriptor, 3, dSerializer, value.fourth)
            encodeSerializableElement(eSerializer.descriptor, 4, eSerializer, value.fifth)
        }
    }
}

@Serializable(with = QuintSerializer::class)
data class Quint<out A, out B, out C, out D, out E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) {

    override fun toString(): String = "($first, $second, $third, ${fourth}, ${fifth})"

}


fun <T> UniformQuint<T>.toList() = listOf(first, second, third, fourth, fifth)
inline fun <reified T> UniformQuint<T>.toTypedArray() = toList().toTypedArray<T>()


fun <T, R> UniformQuint<T>.map(op: (T) -> R): UniformQuint<R> =
    Quint(op(first), op(second), op(third), op(fourth), op(fifth))

fun <T> UniformQuint<T>.forEach(op: Consume<T>) {
    op(first)
    op(second)
    op(third)
    op(fourth)
    op(fifth)
}

operator fun <T> UniformQuint<T>.contains(value: T) = toList().contains(value)