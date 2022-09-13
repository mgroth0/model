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


data class PixelIndex(val x: Int, val y: Int)


