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


data class PixelIndex(val x: Int, val y: Int)