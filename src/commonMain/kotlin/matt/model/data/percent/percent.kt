package matt.model.data.percent

import matt.lang.convert.BiConverter
import matt.model.data.mathable.DoubleWrapper
import kotlin.jvm.JvmInline

interface PercentIdea

val Int.percent get() = Percent(this)

@JvmInline
value class Percent(val percent: Double) : PercentIdea, DoubleWrapper<Percent> {
    constructor(n: Number) : this(n.toDouble())

    override fun fromDouble(d: Double): Percent = Percent(d)

    override val asDouble: Double
        get() = percent
}




object PercentDoubleConverter : BiConverter<Percent, Double> {
    override fun convertToB(a: Percent): Double = a.asDouble

    override fun convertToA(b: Double): Percent = Percent(b)
}
