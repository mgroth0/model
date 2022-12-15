package matt.model.data.mathable

import matt.model.data.interp.BasicInterpolatable
import kotlin.math.abs


interface Mathable<M: Mathable<M>> {
  operator fun div(n: Number): M
  operator fun div(m: M): Number
  operator fun times(n: Number): M
  operator fun plus(m: M): M
  operator fun minus(m: M): M

  val abs: M

}




interface MathAndComparable<M: MathAndComparable<M>>: Mathable<M>, Comparable<M>


interface NumberWrapper<N: NumberWrapper<N>> {
  val asNumber: Number
  val isZero: Boolean
  val isNaN: Boolean
  val isInfinite: Boolean
  fun of(n: Int): N
}

interface DoubleWrapper<M: DoubleWrapper<M>>: MathAndComparable<M>, BasicInterpolatable<M>, NumberWrapper<M> {

  override fun interpolate(endValue: BasicInterpolatable<*>, t: Double): M {

	@Suppress("UNCHECKED_CAST") if (t <= 0.0) return this as M
	@Suppress("UNCHECKED_CAST") return if (t >= 1.0) (endValue as M) else fromDouble(
	  asDouble + ((endValue as DoubleWrapper<M>).asDouble - asDouble)*t,
	)
  }

  override fun compareTo(other: M): Int {
	return asDouble.compareTo(other.asDouble)
  }

  fun fromDouble(d: Double): M
  val asDouble: Double
  override val asNumber: Number
	get() = asDouble
  override fun plus(m: M): M {
	return fromDouble(asDouble + m.asDouble)
  }

  override fun minus(m: M): M {
	return fromDouble(asDouble - m.asDouble)
  }

  override fun div(n: Number): M {
	return fromDouble(asDouble/n.toDouble())
  }

  override fun times(n: Number): M {
	return fromDouble(asDouble*n.toDouble())
  }

  override fun div(m: M): Number {
	return asDouble/m.asDouble
  }

  override val isInfinite: Boolean
	get() = asDouble.isInfinite()

  override val isNaN: Boolean
	get() = asDouble.isNaN()

  override val isZero: Boolean
	get() = asDouble == 0.0

  override val abs: M
	get() = fromDouble(abs(asDouble))

  override fun of(n: Int): M {
	return fromDouble(n.toDouble())
  }
}


interface FloatWrapper<M: FloatWrapper<M>>: MathAndComparable<M>, NumberWrapper<M> {
  override fun compareTo(other: M): Int {
	return asFloat.compareTo(other.asFloat)
  }

  fun fromFloat(d: Float): M
  val asFloat: Float
  override val asNumber: Number
	get() = asFloat
  override fun plus(m: M): M {
	return fromFloat(asFloat + m.asFloat)
  }

  override fun minus(m: M): M {
	return fromFloat(asFloat - m.asFloat)
  }

  override fun div(m: M): Number {
	return asFloat/m.asFloat
  }

  override fun div(n: Number): M {
	return fromFloat(asFloat/n.toFloat())
  }

  override fun times(n: Number): M {
	return fromFloat(asFloat*n.toFloat())
  }

  override val isNaN: Boolean
	get() = asFloat.isNaN()

  override val isZero: Boolean
	get() = asFloat == 0.0f

  override val abs: M
	get() = fromFloat(abs(asFloat))

  override val isInfinite: Boolean
	get() = asFloat.isInfinite()

  override fun of(n: Int): M {
	return fromFloat(n.toFloat())
  }

}


data class Fraction<N: Mathable<N>, D: Mathable<D>>(val n: N, val d: D)

operator fun <M: Mathable<M>, D: Mathable<D>> M.div(d: D) = Fraction(n = this, d = d)


interface IntWrapper<M: IntWrapper<M>>: MathAndComparable<M> {
  override fun compareTo(other: M): Int {
	return asInt.compareTo(other.asInt)
  }

  fun fromInt(d: Int): M
  val asInt: Int
  override fun plus(m: M): M {
	return fromInt(asInt + m.asInt)
  }

  override fun minus(m: M): M {
	return fromInt(asInt - m.asInt)
  }

  override fun div(n: Number): M {
	return fromInt(asInt/n.toInt())
  }

  override fun times(n: Number): M {
	return fromInt(asInt*n.toInt())
  }

  override fun div(m: M): Number {
	return asInt/m.asInt
  }

}