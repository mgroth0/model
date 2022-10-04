package matt.model.latch.asyncloaded

import matt.model.latch.SimpleLatch
import java.lang.Thread.State

class DaemonLoadedValueOp<T>(name: String? = null, private val op: ()->T): Async<T>() {

  private val thread by lazy {
	Thread {
	  value = op()
	  latch.open()
	  if (name != null) println("finished loading $name")
	}.apply {
	  isDaemon = true
	}
  }

  @Synchronized
  fun startLoading() {
	require(latch.isClosed)
	require(thread.state == State.NEW)
	thread.start()
  }
}

open class SyncLoadedValueOp<T>(private val op: ()->T): Async<T>() {

  @Synchronized
  fun calc(): T {
	require(latch.isClosed)
	value = op()
	latch.open()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }
}

open class LoadedValueSlot<T>(): Async<T>() {


  @Synchronized
  fun putLoadedValue(t: T) {
	require(latch.isClosed)
	value = t
	latch.open()
  }

}

/*is this just a future? or a modified lazy?*/
abstract class Async<T> {
  protected val latch = SimpleLatch()
  protected var value: T? = null

  fun await(): T {
	latch.await()
	@Suppress("UNCHECKED_CAST")
	return value as T
  }
}
