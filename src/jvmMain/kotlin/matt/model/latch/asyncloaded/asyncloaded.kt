package matt.model.latch.asyncloaded

import matt.model.latch.SimpleLatch

class AsyncLoadingValue<T>() {
  private val latch = SimpleLatch()
  private var value: T? = null

  @Synchronized
  fun putLoadedValue(t: T) {
	require(latch.isClosed)
	value = t
	latch.open()
  }

  fun await(): T {
	latch.await()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }
}