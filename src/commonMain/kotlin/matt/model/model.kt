package matt.model


interface Searchable {
  val searchSeq: Sequence<String>
}

interface Command {
  fun run(arg: String)
}


enum class ExitStatus {
  CONTINUE, EXIT
}

//interface SingleArgCommandWithExitStatus {
//  fun run(arg: String): ExitStatus
//}
//interface SingleArgCommandWithStringResult {
//  fun run(arg: String): String
//}


interface Converter<A, B> {
  fun convertToB(a: A): B
  fun A.toB() = convertToB(this)
  fun convertToA(b: B): A
  fun B.toA() = convertToA(this)
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


data class PixelIndex(val x: Int, val y: Int)


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