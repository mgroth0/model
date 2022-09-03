package matt.model.byte

import matt.model.byte.ByteSize.ByteUnit
import matt.model.byte.ByteSize.ByteUnit.B
import matt.model.byte.ByteSize.ByteUnit.GB
import matt.model.byte.ByteSize.ByteUnit.KB
import matt.model.byte.ByteSize.ByteUnit.MB
import matt.model.byte.ByteSize.ByteUnit.TB
import kotlin.jvm.JvmName


data class ByteSize(val bytes: Long): Comparable<ByteSize> {
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


  private val bestUnit by lazy {
	ByteUnit.values().reversed().firstOrNull {
	  val rep = unitRep(it)
	  rep > 1 || rep < -1
	} ?: B
  }

  val formatted by lazy { FormattedByteSize(unitRep(bestUnit), bestUnit) }
  override fun compareTo(other: ByteSize) = this.bytes.compareTo(other.bytes)
  override fun toString() = formatted.toString()
  operator fun plus(other: ByteSize) = ByteSize(bytes + other.bytes)
  operator fun minus(other: ByteSize) = ByteSize(bytes - other.bytes)
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


