package matt.model.data.byte

import kotlinx.serialization.Serializable
import matt.model.data.byte.ByteSize.BinaryByteUnit
import matt.model.data.byte.ByteSize.BinaryByteUnit.B
import matt.model.data.byte.ByteSize.BinaryByteUnit.GiB
import matt.model.data.byte.ByteSize.BinaryByteUnit.KiB
import matt.model.data.byte.ByteSize.BinaryByteUnit.MiB
import matt.model.data.byte.ByteSize.BinaryByteUnit.TiB
import matt.model.data.byte.ByteSize.DecimalByteUnit
import matt.model.data.byte.ByteSize.DecimalByteUnit.GB
import matt.model.data.byte.ByteSize.DecimalByteUnit.KB
import matt.model.data.byte.ByteSize.DecimalByteUnit.MB
import matt.model.data.byte.ByteSize.DecimalByteUnit.TB
import matt.model.data.mathable.MathAndComparable
import matt.model.data.mathable.NumberWrapper
import matt.model.op.convert.Converter
import kotlin.jvm.JvmName
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToLong


val Int.decimalBytes get() = ByteSize(this)
val Int.kilobytes get() = ByteSize(this * KB.size)
val Int.megabytes get() = ByteSize(this * MB.size)
val Int.gigabytes get() = ByteSize(this * GB.size)
val Int.terabytes get() = ByteSize(this * TB.size)

val Long.decimalBytes get() = ByteSize(this)
val Long.kilobytes get() = ByteSize(this * KB.size)
val Long.megabytes get() = ByteSize(this * MB.size)
val Long.gigabytes get() = ByteSize(this * GB.size)
val Long.terabytes get() = ByteSize(this * TB.size)

val Int.bytes get() = ByteSize(this)
val Int.kibibytes get() = ByteSize(this * KiB.size)
val Int.mebibytes get() = ByteSize(this * MiB.size)
val Int.gibibytes get() = ByteSize(this * GiB.size)
val Int.tebibytes get() = ByteSize(this * TiB.size)

val Long.bytes get() = ByteSize(this)
val Long.kibibytes get() = ByteSize(this * KiB.size)
val Long.mebibytes get() = ByteSize(this * MiB.size)
val Long.gibibytes get() = ByteSize(this * GiB.size)
val Long.tebibytes get() = ByteSize(this * TiB.size)

fun Double.isWhole() = ceil(this) == floor(this)

@Serializable
data class ByteSize(val bytes: Long) : MathAndComparable<ByteSize>, NumberWrapper<ByteSize> {

    constructor(bytes: Number) : this(bytes.toLong())


    enum class BinaryByteUnit(val size: Long) {
        B(1), KiB(1024), MiB(KiB.size * KiB.size), GiB(MiB.size * KiB.size), TiB(GiB.size * KiB.size);


    }

    val BinaryByteUnit.traditionalName
        get() = when (this) {
            B   -> DecimalByteUnit.B.name
            KiB -> DecimalByteUnit.KB.name
            MiB -> DecimalByteUnit.MB.name
            GiB -> DecimalByteUnit.GB.name
            TiB -> DecimalByteUnit.TB.name
        }

    enum class DecimalByteUnit(val size: Long) {
        B(1), KB(1000), MB(KB.size * KB.size), GB(MB.size * KB.size), TB(GB.size * KB.size);
    }

    private fun binaryUnitRep(u: BinaryByteUnit) = bytes.toDouble() / u.size
    private fun decimalUnitRep(u: DecimalByteUnit) = bytes.toDouble() / u.size


    val b by lazy { binaryUnitRep(B) }
    val kiB by lazy { binaryUnitRep(KiB) }
    val miB by lazy { binaryUnitRep(MiB) }
    val giB by lazy { binaryUnitRep(GiB) }
    val tiB by lazy { binaryUnitRep(TiB) }

    val decimalB by lazy { decimalUnitRep(DecimalByteUnit.B) }
    val kb by lazy { decimalUnitRep(KB) }
    val mb by lazy { decimalUnitRep(MB) }
    val gb by lazy { decimalUnitRep(GB) }
    val tb by lazy { decimalUnitRep(TB) }


    val bestBinaryUnit by lazy {
        BinaryByteUnit.values().reversed().firstOrNull {
            val rep = binaryUnitRep(it)
            rep > 1 || rep < -1
        } ?: B
    }
    val bestDecimalUnit by lazy {
        DecimalByteUnit.values().reversed().firstOrNull {
            val rep = decimalUnitRep(it)
            rep > 1 || rep < -1
        } ?: DecimalByteUnit.B
    }


    val largestWholeBinaryUnit by lazy {
        BinaryByteUnit.values().reversed().firstOrNull {
            val rep = binaryUnitRep(it)
            rep.isWhole()
        } ?: B
    }
    val largestWholeDecimalUnit by lazy {
        DecimalByteUnit.values().reversed().firstOrNull {
            val rep = decimalUnitRep(it)
            rep.isWhole()
        } ?: DecimalByteUnit.B
    }


    val formattedBinary by lazy {
        FormattedBinaryByteSize(
            binaryUnitRep(bestBinaryUnit),
            bestBinaryUnit,
            includeSpace = true,
            singleLetterUnit = false
        )
    }
    val formattedDecimal by lazy {
        FormattedDecimalByteSize(
            decimalUnitRep(bestDecimalUnit),
            bestDecimalUnit,
            includeSpace = false,
            singleLetterUnit = false
        )
    }
    val formattedBinaryNoSpaceNoDecimalsAndSingleLetterUnit by lazy {
        FormattedBinaryByteSize(
            binaryUnitRep(largestWholeBinaryUnit),
            largestWholeBinaryUnit,
            includeSpace = false,
            singleLetterUnit = true
        )
    }
    val formattedDecimalNoSpaceNoDecimalsAndSingleLetterUnit by lazy {
        FormattedDecimalByteSize(
            decimalUnitRep(largestWholeDecimalUnit),
            largestWholeDecimalUnit,
            includeSpace = false,
            singleLetterUnit = true
        )
    }


    override fun toString() = formattedBinary.toString()
    override fun compareTo(other: ByteSize) = this.bytes.compareTo(other.bytes)
    override fun div(n: Number): ByteSize = ByteSize(bytes / n.toLong())
    override fun times(n: Number) = ByteSize(bytes * n.toLong())
    override val isZero: Boolean
        get() = bytes == 0L
    override val isNaN: Boolean
        get() = false
    override val isInfinite: Boolean
        get() = false
    override val abs: ByteSize
        get() = ByteSize(abs(bytes))


    override val asNumber: Number
        get() = bytes

    override fun of(n: Int): ByteSize {
        return ByteSize(n)
    }

    override fun div(m: ByteSize) = bytes / m.bytes
    override operator fun plus(m: ByteSize) = ByteSize(bytes + m.bytes)
    override operator fun minus(m: ByteSize) = ByteSize(bytes - m.bytes)
}


@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("sumOfBinaryByteSize")
inline fun <T> Iterable<T>.sumOf(selector: (T) -> ByteSize): ByteSize {
    var sum: ByteSize = ByteSize(0)
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

class FormattedBinaryByteSize(
    val num: Double,
    val unit: BinaryByteUnit,
    includeSpace: Boolean,
    singleLetterUnit: Boolean
) {
    private val unitName = if (singleLetterUnit) unit.name.first() else unit.name
    private val space = if (includeSpace) ' '.toString() else ""
    override fun toString(): String {
        return if (unit == B) "$num$space$unitName"
        else "${(num * 1000).roundToLong() / 1000}$space$unitName"
    }
}

class FormattedDecimalByteSize(
    val num: Double,
    val unit: DecimalByteUnit,
    includeSpace: Boolean,
    singleLetterUnit: Boolean
) {
    private val unitName = if (singleLetterUnit) unit.name.first() else unit.name
    private val space = if (includeSpace) ' '.toString() else ""
    override fun toString(): String {
        return if (unit == DecimalByteUnit.B) "$num$space$unitName"
        else "${(num * 1000).roundToLong() / 1000}$space$unitName"
    }
}


object BinaryByteSizeDoubleConverter : Converter<ByteSize, Double> {
    override fun convertToB(a: ByteSize): Double {
        return a.bytes.toDouble()
    }

    override fun convertToA(b: Double): ByteSize {
        return ByteSize(b.toLong())
    }

}
