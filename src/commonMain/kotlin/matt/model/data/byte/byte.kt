package matt.model.data.byte

import kotlinx.serialization.Serializable
import matt.model.data.byte.ByteSize.ByteUnit
import matt.model.data.byte.ByteSize.ByteUnit.B
import matt.model.data.byte.ByteSize.ByteUnit.GB
import matt.model.data.byte.ByteSize.ByteUnit.KB
import matt.model.data.byte.ByteSize.ByteUnit.MB
import matt.model.data.byte.ByteSize.ByteUnit.TB
import matt.model.data.mathable.MathAndComparable
import matt.model.data.mathable.NumberWrapper
import matt.model.op.convert.Converter
import kotlin.jvm.JvmName
import kotlin.math.abs


val Int.bytes get() = ByteSize(this)
val Int.kilobytes get() = ByteSize(this*KB.size)
val Int.megabytes get() = ByteSize(this*MB.size)
val Int.gigabytes get() = ByteSize(this*GB.size)
val Int.terabytes get() = ByteSize(this*TB.size)

val Long.bytes get() = ByteSize(this)
val Long.kilobytes get() = ByteSize(this*KB.size)
val Long.megabytes get() = ByteSize(this*MB.size)
val Long.gigabytes get() = ByteSize(this*GB.size)
val Long.terabytes get() = ByteSize(this*TB.size)

@Serializable
data class ByteSize(val bytes: Long): MathAndComparable<ByteSize>, NumberWrapper<ByteSize> {

  constructor(bytes: Number): this(bytes.toLong())


  enum class ByteUnit(val size: Long) {
	B(1), KB(1024), MB(KB.size*KB.size), GB(MB.size*KB.size), TB(GB.size*KB.size)
  }

  private fun unitRep(u: ByteUnit) = bytes.toDouble()/u.size


  val b by lazy { unitRep(B) }
  val kb by lazy { unitRep(KB) }
  val mb by lazy { unitRep(MB) }
  val gb by lazy { unitRep(GB) }
  val tb by lazy { unitRep(TB) }


  val bestUnit by lazy {
	ByteUnit.values().reversed().firstOrNull {
	  val rep = unitRep(it)
	  rep > 1 || rep < -1
	} ?: B
  }


  val formatted by lazy { FormattedByteSize(unitRep(bestUnit), bestUnit) }
  override fun toString() = formatted.toString()
  override fun compareTo(other: ByteSize) = this.bytes.compareTo(other.bytes)
  override fun div(n: Number): ByteSize = ByteSize(bytes/n.toLong())
  override fun times(n: Number) = ByteSize(bytes*n.toLong())
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

  override fun div(m: ByteSize) = bytes/m.bytes
  override operator fun plus(m: ByteSize) = ByteSize(bytes + m.bytes)
  override operator fun minus(m: ByteSize) = ByteSize(bytes - m.bytes)
}


@OptIn(kotlin.experimental.ExperimentalTypeInference::class)
@OverloadResolutionByLambdaReturnType
@JvmName("sumOfByteSize")
inline fun <T> Iterable<T>.sumOf(selector: (T)->ByteSize): ByteSize {
  var sum: ByteSize = ByteSize(0)
  for (element in this) {
	sum += selector(element)
  }
  return sum
}

expect class FormattedByteSize(num: Double, unit: ByteUnit)


object ByteSizeDoubleConverter: Converter<ByteSize, Double> {
  override fun convertToB(a: ByteSize): Double {
	return a.bytes.toDouble()
  }

  override fun convertToA(b: Double): ByteSize {
	return ByteSize(b.toLong())
  }

}
