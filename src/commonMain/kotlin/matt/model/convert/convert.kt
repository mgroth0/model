package matt.model.convert

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
  val inverted by lazy { invert() }
  override fun toString(t: String?): String {
	return t ?: ""
  }

  override fun fromString(s: String): String {
	return s
  }

}

interface Converter<A, B> {
  fun convertToB(a: A): B
  fun A.toB() = convertToB(this)
  fun convertToA(b: B): A
  fun B.toA() = convertToA(this)
  fun invert(): Converter<B, A> {
	val outer = this
	return object: Converter<B, A> {

	  override fun convertToB(a: B): A {
		return outer.convertToA(a)
	  }

	  override fun convertToA(b: A): B {
		return outer.convertToB(b)
	  }

	}
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