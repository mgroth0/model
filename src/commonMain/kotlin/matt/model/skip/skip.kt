package matt.model.skip

class Skipper(
  val numElementsToSkip: Int = 0
) {
  init {
	require(numElementsToSkip >= 0)
  }

  private var skipIndex = -1
  fun <E> skipThrough(list: List<E>): List<E> = list.filter {
	skipIndex++
	if (skipIndex > numElementsToSkip) skipIndex = 0
	skipIndex == numElementsToSkip
  }
}