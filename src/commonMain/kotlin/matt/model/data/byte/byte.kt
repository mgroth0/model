package matt.model.data.byte

import kotlinx.serialization.Serializable
import matt.lang.convert.BiConverter
import matt.model.data.byte.ByteSize.BinaryByteUnit.B
import matt.model.data.byte.ByteSize.BinaryByteUnit.GiB
import matt.model.data.byte.ByteSize.BinaryByteUnit.KiB
import matt.model.data.byte.ByteSize.BinaryByteUnit.MiB
import matt.model.data.byte.ByteSize.BinaryByteUnit.TiB
import matt.model.data.byte.ByteSize.ByteUnit
import matt.model.data.byte.ByteSize.DecimalByteUnit
import matt.model.data.byte.ByteSize.DecimalByteUnit.GB
import matt.model.data.byte.ByteSize.DecimalByteUnit.KB
import matt.model.data.byte.ByteSize.DecimalByteUnit.MB
import matt.model.data.byte.ByteSize.DecimalByteUnit.TB
import matt.model.data.mathable.MathAndComparable
import matt.model.data.mathable.NumberWrapper
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

    companion object {
        val ZERO = ByteSize(0)
    }

    interface ByteUnit {
        val name: String
        val size: Long
    }

    enum class BinaryByteUnit(override val size: Long) : ByteUnit {
        B(1), KiB(1024), MiB(KiB.size * KiB.size), GiB(MiB.size * KiB.size), TiB(GiB.size * KiB.size);


    }

    val BinaryByteUnit.traditionalName
        get() = when (this) {
            B   -> DecimalByteUnit.B.name
            KiB -> KB.name
            MiB -> MB.name
            GiB -> GB.name
            TiB -> TB.name
        }

    enum class DecimalByteUnit(override val size: Long) : ByteUnit {
        B(1), KB(1000), MB(KB.size * KB.size), GB(MB.size * KB.size), TB(GB.size * KB.size);
    }

    private fun unitRep(u: ByteUnit) = bytes.toDouble() / u.size


    val b by lazy { unitRep(B) }
    val kiB by lazy { unitRep(KiB) }
    val miB by lazy { unitRep(MiB) }
    val giB by lazy { unitRep(GiB) }
    val tiB by lazy { unitRep(TiB) }

    val decimalB by lazy { unitRep(DecimalByteUnit.B) }
    val kb by lazy { unitRep(KB) }
    val mb by lazy { unitRep(MB) }
    val gb by lazy { unitRep(GB) }
    val tb by lazy { unitRep(TB) }


    val bestBinaryUnit by lazy {
        BinaryByteUnit.entries.reversed().firstOrNull {
            val rep = unitRep(it)
            rep > 1 || rep < -1
        } ?: B
    }
    val bestDecimalUnit by lazy {
        DecimalByteUnit.entries.reversed().firstOrNull {
            val rep = unitRep(it)
            rep > 1 || rep < -1
        } ?: DecimalByteUnit.B
    }


    val largestWholeBinaryUnit by lazy {
        BinaryByteUnit.entries.reversed().firstOrNull {
            val rep = unitRep(it)
            rep.isWhole()
        } ?: B
    }
    val largestWholeDecimalUnit by lazy {
        DecimalByteUnit.entries.reversed().firstOrNull {
            val rep = unitRep(it)
            rep.isWhole()
        } ?: DecimalByteUnit.B
    }


    val formattedBinary by lazy {
        FormattedByteSize(
            unitRep(bestBinaryUnit),
            bestBinaryUnit,
            includeSpace = true,
            singleLetterUnit = false
        )
    }
    val formattedDecimal by lazy {
        FormattedByteSize(
            unitRep(bestDecimalUnit),
            bestDecimalUnit,
            includeSpace = false,
            singleLetterUnit = false
        )
    }
    val formattedBinaryNoSpaceNoDecimalsAndSingleLetterUnit by lazy {
        FormattedByteSize(
            unitRep(largestWholeBinaryUnit),
            largestWholeBinaryUnit,
            includeSpace = false,
            singleLetterUnit = true
        )
    }
    val formattedDecimalNoSpaceNoDecimalsAndSingleLetterUnit by lazy {
        FormattedByteSize(
            unitRep(largestWholeDecimalUnit),
            largestWholeDecimalUnit,
            includeSpace = false,
            singleLetterUnit = true
        )
    }


    override fun toString() = formattedBinary.toString()
    override fun compareTo(other: ByteSize) = this.bytes.compareTo(other.bytes)
    override fun div(n: Number): ByteSize = ByteSize(bytes / n.toDouble())

    override fun times(n: Number) = ByteSize(bytes * n.toDouble())

    override val isZero: Boolean
        get() = bytes == 0L
    override val isPositive: Boolean
        get() = bytes > 0
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

    override fun unaryMinus(): ByteSize {
        return ByteSize(-bytes)
    }

    override fun div(m: ByteSize) = bytes / m.bytes
    override operator fun plus(m: ByteSize) = ByteSize(bytes + m.bytes)
    override operator fun minus(m: ByteSize) = ByteSize(bytes - m.bytes)


    fun roundToNearest(unit: ByteUnit): ByteSize {
        val minPossible = floor(bytes.toDouble() / unit.size)
        val maxPossible = minPossible + 1
        val halfwayPoint = listOf(minPossible, maxPossible).sum() / 2.0
        return if (bytes >= halfwayPoint) ByteSize(maxPossible * unit.size)
        else ByteSize(minPossible * unit.size)
    }


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

class FormattedByteSize(
    val num: Double,
    val unit: ByteUnit,
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


object BinaryByteSizeDoubleConverter : BiConverter<ByteSize, Double> {
    override fun convertToB(a: ByteSize): Double {
        return a.bytes.toDouble()
    }

    override fun convertToA(b: Double): ByteSize {
        return ByteSize(b.toLong())
    }

}
