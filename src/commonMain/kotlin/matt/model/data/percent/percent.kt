package matt.model.data.percent

import matt.model.data.mathable.DoubleWrapper
import matt.model.op.convert.Converter
import kotlin.jvm.JvmInline

interface PercentIdea

val Int.percent get() = Percent(this)

@JvmInline
value class Percent(val percent: Double): PercentIdea, DoubleWrapper<Percent> {
  constructor(n: Number): this(n.toDouble())

  override fun fromDouble(d: Double): Percent {
	return Percent(d)
  }

  override val asDouble: Double
	get() = percent
}

object PercentDoubleConverter: Converter<Percent, Double> {
  override fun convertToB(a: Percent): Double {
	return a.asDouble
  }

  override fun convertToA(b: Double): Percent {
	return Percent(b)
  }

}
