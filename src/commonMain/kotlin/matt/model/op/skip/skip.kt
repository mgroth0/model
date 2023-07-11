package matt.model.op.skip

import matt.lang.require.requireNonNegative

class Skipper(
  val numElementsToSkip: Int = 0
) {
  init {
	requireNonNegative(numElementsToSkip)
  }

  private var skipIndex = -1
  fun <E> skipThrough(list: Iterable<E>): List<E> = list.filter {
	skipIndex++
	if (skipIndex > numElementsToSkip) skipIndex = 0
	skipIndex == numElementsToSkip
  }
}