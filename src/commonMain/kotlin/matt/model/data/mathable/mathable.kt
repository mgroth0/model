package matt.model.data.mathable

import matt.lang.anno.Open
import matt.lang.jpy.ExcludeFromPython
import matt.model.data.interp.BasicInterpolatable
import kotlin.jvm.JvmInline
import kotlin.math.abs
import kotlin.math.absoluteValue


interface Mathable<M : Mathable<M>> {
    operator fun div(n: Number): M
    operator fun div(m: M): Number
    operator fun times(n: Number): M
    operator fun plus(m: M): M
    operator fun minus(m: M): M

    val abs: M

    @ExcludeFromPython
    infix fun floatingPointDiv(m: M): Double
}


interface MathAndComparable<M : MathAndComparable<M>> : Mathable<M>, Comparable<M>


interface NumberWrapper<N : NumberWrapper<N>> : MathAndComparable<N> {
    val asNumber: Number
    val isZero: Boolean
    val isPositive: Boolean
    val isNaN: Boolean
    val isInfinite: Boolean
    fun of(n: Int): N

    @Open
    override fun floatingPointDiv(m: N): Double = (asNumber.toDouble() / m.asNumber.toDouble())

    operator fun unaryMinus(): N
}

val NumberWrapper<*>.isNegative get() = !(isPositive || isZero || isNaN)


interface DoubleWrapper<M : DoubleWrapper<M>> : MathAndComparable<M>, BasicInterpolatable<M>, NumberWrapper<M> {

    @Open
    @ExcludeFromPython
    override fun interpolate(
        endValue: BasicInterpolatable<*>,
        t: Double
    ): M {

        @Suppress("UNCHECKED_CAST") if (t <= 0.0) return this as M
        @Suppress("UNCHECKED_CAST") return if (t >= 1.0) (endValue as M) else fromDouble(
            asDouble + ((endValue as DoubleWrapper<M>).asDouble - asDouble) * t
        )
    }

    @Open
    override fun compareTo(other: M): Int = asDouble.compareTo(other.asDouble)

    fun fromDouble(d: Double): M
    val asDouble: Double

    @Open
    override val asNumber: Double
        get() = asDouble

    @Open
    override fun plus(m: M): M = fromDouble(asDouble + m.asDouble)

    @Open
    override fun minus(m: M): M = fromDouble(asDouble - m.asDouble)

    @Open
    @ExcludeFromPython
    override fun div(n: Number): M = fromDouble(asDouble / n.toDouble())

    @Open
    @ExcludeFromPython
    override fun times(n: Number): M = fromDouble(asDouble * n.toDouble())

    @Open
    @ExcludeFromPython
    override fun div(m: M): Double = asDouble / m.asDouble

    @Open
    override val isInfinite: Boolean
        get() = asDouble.isInfinite()

    @Open
    override val isNaN: Boolean
        get() = asDouble.isNaN()

    @Open
    override val isZero: Boolean
        get() = asDouble == 0.0

    @Open
    override val isPositive: Boolean
        get() = asDouble > 0.0

    @Open
    override val abs: M
        get() = fromDouble(abs(asDouble))

    @Open
    override fun of(n: Int): M = fromDouble(n.toDouble())

    @Open
    override operator fun unaryMinus() = fromDouble(-asDouble)
}

@ExcludeFromPython
infix fun <M : DoubleWrapper<M>> M.minusFix(m: M): M = fromDouble(asDouble - m.asDouble)

@ExcludeFromPython
infix fun <M : DoubleWrapper<M>> M.plusFix(m: M): M = fromDouble(asDouble + m.asDouble)

@JvmInline
value class BasicDoubleWrapper(override val asDouble: Double) : DoubleWrapper<BasicDoubleWrapper> {
    override fun fromDouble(d: Double): BasicDoubleWrapper = BasicDoubleWrapper(d)
}


interface FloatWrapper<M : FloatWrapper<M>> : MathAndComparable<M>, NumberWrapper<M> {
    @Open
    override fun compareTo(other: M): Int = asFloat.compareTo(other.asFloat)

    fun fromFloat(d: Float): M
    val asFloat: Float

    @Open
    override val asNumber: Number
        get() = asFloat

    @Open
    override fun plus(m: M): M = fromFloat(asFloat + m.asFloat)

    @Open
    override fun minus(m: M): M = fromFloat(asFloat - m.asFloat)

    @Open
    override fun div(m: M): Float = asFloat / m.asFloat

    @Open
    override fun div(n: Number): M = fromFloat(asFloat / n.toFloat())

    @Open
    override fun times(n: Number): M = fromFloat(asFloat * n.toFloat())

    @Open
    override val isNaN: Boolean
        get() = asFloat.isNaN()

    @Open
    override val isZero: Boolean
        get() = asFloat == 0.0f

    @Open
    override val isPositive: Boolean
        get() = asFloat > 0.0

    @Open
    override val abs: M
        get() = fromFloat(abs(asFloat))

    @Open
    override val isInfinite: Boolean
        get() = asFloat.isInfinite()

    @Open
    override fun of(n: Int): M = fromFloat(n.toFloat())

    @Open
    override operator fun unaryMinus() = fromFloat(-asFloat)
}

@JvmInline
value class BasicFloatWrapper(override val asFloat: Float) : FloatWrapper<BasicFloatWrapper> {
    override fun fromFloat(d: Float): BasicFloatWrapper = BasicFloatWrapper(d)
}


data class Fraction<N : Mathable<N>, D : Mathable<D>>(
    val n: N,
    val d: D
)

operator fun <M : Mathable<M>, D : Mathable<D>> M.div(d: D) = Fraction(n = this, d = d)


interface IntWrapper<M : IntWrapper<M>> : NumberWrapper<M> {

    @Open
    override fun compareTo(other: M): Int = asInt.compareTo(other.asInt)

    fun fromInt(d: Int): M
    val asInt: Int

    @Open
    @ExcludeFromPython
    override fun plus(m: M): M = fromInt(asInt + m.asInt)

    @Open
    @ExcludeFromPython
    override fun minus(m: M): M = fromInt(asInt - m.asInt)

    @Open
    @ExcludeFromPython
    override fun div(n: Number): M = fromInt(asInt / n.toInt())

    @Open
    @ExcludeFromPython
    override fun times(n: Number): M = fromInt(asInt * n.toInt())

    @Open
    @ExcludeFromPython
    override fun div(m: M): Number = asInt / m.asInt

    @Open
    @ExcludeFromPython
    override val abs: M
        get() = fromInt(asInt.absoluteValue)

    @Open
    @ExcludeFromPython
    override val asNumber: Number
        get() = asInt

    @Open
    override val isInfinite: Boolean
        get() = false

    @Open
    override val isNaN: Boolean
        get() = false

    @Open
    override val isZero: Boolean
        get() = asInt == 0

    @Open
    override val isPositive: Boolean
        get() = asInt > 0

    @Open
    override fun of(n: Int): M = fromInt(n)

    @Open
    override operator fun unaryMinus() = fromInt(-asInt)
}
