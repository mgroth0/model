package matt.model.convert

import matt.model.byte.ByteSize
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> toStringConverter(op: (T)->String) = object: StringConverter<T> {
  override fun toString(t: T): String {
	return op(t)
  }

  override fun fromString(s: String): T {
	TODO("Not yet implemented")
  }

}

fun <T> fromStringConverter(op: (String)->T) = object: StringConverter<T> {
  override fun toString(t: T): String {
	TODO("Not yet implemented")
  }

  override fun fromString(s: String): T {
	return op(s)
  }

}


object NullToBlankStringConverter: StringConverter<String?> {
  override fun toString(t: String?): String {
	return t ?: ""
  }

  override fun fromString(s: String): String {
	return s
  }

}

interface Converter<A, B> {

  companion object {
	/*TODO: should be WeakMap*/
	val invertedConverters = mutableMapOf<Converter<*, *>, Converter<*, *>>()
  }

  fun convertToB(a: A): B
  fun A.toB() = convertToB(this)
  fun convertToA(b: B): A
  fun B.toA() = convertToA(this)
  fun invert(): Converter<B, A> {
	val i = invertedConverters[this]
	@Suppress("UNCHECKED_CAST")
	if (i != null) return i as Converter<B, A>
	val outer = this
	val inv = object: Converter<B, A> {

	  override fun convertToB(a: B): A {
		return outer.convertToA(a)
	  }

	  override fun convertToA(b: A): B {
		return outer.convertToB(b)
	  }

	}
	invertedConverters[this] = inv
	return inv
  }
}

interface StringConverter<T>: Converter<String, T> {
  fun toString(t: T): String
  fun fromString(s: String): T
  override fun convertToA(b: T): String = toString(b)
  override fun convertToB(a: String) = fromString(a)
}

object IntStringConverter: StringConverter<Int> {
  override fun toString(t: Int) = t.toString()
  override fun fromString(s: String) = s.toInt()
}

object MyNumberStringConverter: StringConverter<Number> {
  override fun toString(t: Number): String {
	return t.toString()
  }

  override fun fromString(s: String): Number {
	return s.toDouble()
  }
}


object LongMillisConverter: Converter<Long, Duration> {
  override fun convertToB(a: Long): Duration {
	return a.milliseconds
  }

  override fun convertToA(b: Duration): Long {
	return b.inWholeMilliseconds
  }
}

object DoubleLongConverter: Converter<Double, Long> {
  override fun convertToB(a: Double): Long {
	return a.toLong()
  }

  override fun convertToA(b: Long): Double {
	return b.toDouble()
  }

}


val BYTE_SIZE_FORMATTER = object: StringConverter<Number> {
  override fun toString(t: Number): String {
	return ByteSize(t.toLong()).toString()
  }

  override fun fromString(s: String) = TODO()
}




val RATIO_TO_PERCENT_FORMATTER_NO_DECIMAL = object: StringConverter<Number> {
  override fun toString(t: Number): String {
	return (t.toDouble()*100).toInt().toString() + "%"
  }

  override fun fromString(s: String) = TODO()
}



