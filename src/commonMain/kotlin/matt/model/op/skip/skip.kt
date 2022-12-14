package matt.model.op.skip

class Skipper(
  val numElementsToSkip: Int = 0
) {
  init {
	require(numElementsToSkip >= 0)
  }

  private var skipIndex = -1
  fun <E> skipThrough(list: Iterable<E>): List<E> = list.filter {
	skipIndex++
	if (skipIndex > numElementsToSkip) skipIndex = 0
	skipIndex == numElementsToSkip
  }
}